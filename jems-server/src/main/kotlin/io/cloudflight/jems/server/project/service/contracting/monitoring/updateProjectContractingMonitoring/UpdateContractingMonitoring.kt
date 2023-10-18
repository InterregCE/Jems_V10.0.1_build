package io.cloudflight.jems.server.project.service.contracting.monitoring.updateProjectContractingMonitoring

import io.cloudflight.jems.server.common.audit.fromOldToNewChanges
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.payments.entity.PaymentGroupingId
import io.cloudflight.jems.server.payments.model.regular.toCreate.PaymentFtlsToCreate
import io.cloudflight.jems.server.payments.model.regular.toCreate.PaymentPartnerToCreate
import io.cloudflight.jems.server.payments.service.monitoringFtlsReadyForPayment
import io.cloudflight.jems.server.payments.service.regular.PaymentPersistence
import io.cloudflight.jems.server.project.authorization.CanSetProjectToContracted
import io.cloudflight.jems.server.project.repository.ProjectPersistenceProvider
import io.cloudflight.jems.server.project.service.ProjectVersionPersistence
import io.cloudflight.jems.server.project.service.budget.get_project_budget.GetProjectBudget
import io.cloudflight.jems.server.project.service.contracting.ContractingValidator
import io.cloudflight.jems.server.project.service.contracting.fillEndDateWithDuration
import io.cloudflight.jems.server.project.service.contracting.fillLumpSumsList
import io.cloudflight.jems.server.project.service.contracting.model.ProjectContractingMonitoring
import io.cloudflight.jems.server.project.service.contracting.monitoring.ContractingMonitoringPersistence
import io.cloudflight.jems.server.project.service.lumpsum.ProjectLumpSumPersistence
import io.cloudflight.jems.server.project.service.lumpsum.model.ProjectLumpSum
import io.cloudflight.jems.server.project.service.model.ProjectFull
import io.cloudflight.jems.server.project.service.partner.cofinancing.ProjectPartnerCoFinancingPersistence
import io.cloudflight.jems.server.project.service.projectContractingMonitoringChanged
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportCoFinancingBreakdown.generateCoFinCalculationInputData
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportCoFinancingBreakdown.getCurrentFrom
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.ZonedDateTime

@Service
class UpdateContractingMonitoring(
    private val contractingMonitoringPersistence: ContractingMonitoringPersistence,
    private val projectPersistence: ProjectPersistenceProvider,
    private val versionPersistence: ProjectVersionPersistence,
    private val projectLumpSumPersistence: ProjectLumpSumPersistence,
    private val partnerCoFinancingPersistence: ProjectPartnerCoFinancingPersistence,
    private val getProjectBudget: GetProjectBudget,
    private val validator: ContractingValidator,
    private val auditPublisher: ApplicationEventPublisher,
    private val paymentPersistence: PaymentPersistence,
) : UpdateContractingMonitoringInteractor {

    @CanSetProjectToContracted
    @Transactional
    @ExceptionWrapper(UpdateContractingMonitoringException::class)
    override fun updateContractingMonitoring(
        projectId: Long,
        contractMonitoring: ProjectContractingMonitoring
    ): ProjectContractingMonitoring {
        projectPersistence.getProjectSummary(projectId).let { projectSummary ->
            ContractingValidator.validateProjectStatusForModification(projectSummary)
            validator.validateMonitoringInput(contractMonitoring)

            val version = versionPersistence.getLatestApprovedOrCurrent(projectId = projectId)
            // load old data for audit once the project is already contracted
            val oldMonitoring = contractingMonitoringPersistence.getContractingMonitoring(projectId)
                .fillLumpSumsList(resolveLumpSums = {
                    projectLumpSumPersistence.getLumpSums(projectId = projectId, version)
                })
            val project = projectPersistence.getProject(projectId = projectId, version)
            val updated = contractingMonitoringPersistence.updateContractingMonitoring(
                contractMonitoring.copy(projectId = projectId)
            ).fillEndDateWithDuration(resolveDuration = { project.duration })
                .apply { fastTrackLumpSums = contractMonitoring.fastTrackLumpSums }

            val lumpSumsOrderNrTobeAdded: MutableSet<Int> = mutableSetOf()
            val lumpSumsOrderNrToBeDeleted: MutableSet<Int> = mutableSetOf()

            val fastTrackLumpSums = contractMonitoring.fastTrackLumpSums!!
            updateReadyForPayment(
                projectId = projectId,
                lumpSums = fastTrackLumpSums,
                savedFastTrackLumpSums = oldMonitoring.fastTrackLumpSums!!,
                orderNrsToBeAdded = lumpSumsOrderNrTobeAdded,
                orderNrsToBeDeleted = lumpSumsOrderNrToBeDeleted
            )
            projectLumpSumPersistence.updateLumpSums(projectId, contractMonitoring.fastTrackLumpSums!!)

            updatePaymentRelatedData(
                project,
                lumpSumsOrderNrTobeAdded,
                lumpSumsOrderNrToBeDeleted,
                fastTrackLumpSums = fastTrackLumpSums,
                version = version,
            )
            updateProjectContractedOnDate(updated, projectId)

            if (projectSummary.status.isAlreadyContracted()) {
                val diff = contractMonitoring.getDiff(old = oldMonitoring)
                if (diff.isNotEmpty()) {
                    auditPublisher.publishEvent(
                        projectContractingMonitoringChanged(
                            context = this,
                            project = projectSummary,
                            changes = diff.fromOldToNewChanges()
                        )
                    )
                }
            }
            return updated
        }
    }

    private fun updateProjectContractedOnDate(contractMonitoring: ProjectContractingMonitoring, projectId: Long) {
        projectPersistence.updateProjectContractedOnDates(
            projectId,
            contractMonitoring.addDates.maxByOrNull { addDate -> addDate.number }?.entryIntoForceDate
        )
    }

    private fun updateReadyForPayment(
        projectId: Long,
        lumpSums: List<ProjectLumpSum>,
        savedFastTrackLumpSums: List<ProjectLumpSum>,
        orderNrsToBeAdded: MutableSet<Int>,
        orderNrsToBeDeleted: MutableSet<Int>
    ) {
        lumpSums.forEach {
            val lumpSum = savedFastTrackLumpSums.first { o -> o.orderNr == it.orderNr }
            it.lastApprovedVersionBeforeReadyForPayment = lumpSum.lastApprovedVersionBeforeReadyForPayment
            it.paymentEnabledDate = lumpSum.paymentEnabledDate
            if (lumpSum.readyForPayment != it.readyForPayment) {
                if (contractingMonitoringPersistence
                        .existsSavedInstallment(projectId, lumpSum.programmeLumpSumId, lumpSum.orderNr)
                ) {
                    throw UpdateContractingMonitoringFTLSException()
                }
                if (it.readyForPayment) {
                    orderNrsToBeAdded.add(it.orderNr)
                    it.lastApprovedVersionBeforeReadyForPayment = this.versionPersistence.getLatestApprovedOrCurrent(projectId)
                    it.paymentEnabledDate = ZonedDateTime.now()
                } else {
                    orderNrsToBeDeleted.add(it.orderNr)
                    it.lastApprovedVersionBeforeReadyForPayment = null
                    it.paymentEnabledDate = null
                }
            }
        }
    }

    private fun updatePaymentRelatedData(
        projectOfCorrectVersion: ProjectFull,
        lumpSumOrderNrsToBeAdded: MutableSet<Int>,
        lumpSumOrderNrsToBeDeleted: MutableSet<Int>,
        fastTrackLumpSums: List<ProjectLumpSum>,
        version: String,
    ) {
        val projectId = projectOfCorrectVersion.id!!
        val partnerTotalByPartnerId = getProjectBudget.getBudget(projectId = projectId, version)
            .associate { Pair(it.partner.id!!, it.totalCosts) }

        val ftlsPaymentContributionMetadata = fastTrackLumpSums
            .filter { it.orderNr in lumpSumOrderNrsToBeAdded }
            .associateWith { lumpSum ->
                lumpSum.lumpSumContributions.associateWith { contribution ->
                    getCurrentFrom(
                        generateCoFinCalculationInputData(
                            totalEligibleBudget = partnerTotalByPartnerId[contribution.partnerId]!!,
                            currentValueToSplit = contribution.amount,
                            coFinancing = partnerCoFinancingPersistence.getCoFinancingAndContributions(contribution.partnerId, version),
                        )
                    )
                }
            }

        if (lumpSumOrderNrsToBeAdded.isNotEmpty()) {
            paymentPersistence.storePartnerContributionsWhenReadyForPayment(
                ftlsPaymentContributionMetadata.onlyPartnerContributions(projectId)
            )

            val ftlsByFund = ftlsPaymentContributionMetadata.sumByFund()
            val paymentsToUpdate = paymentPersistence // TODO decode what is this query doing ???
                .getAmountPerPartnerByProjectIdAndLumpSumOrderNrIn(projectId, lumpSumOrderNrsToBeAdded)
                .groupBy { PaymentGroupingId(it.orderNr, it.programmeFundId) }
                .mapValues { (id, partnerPayments) ->
                    PaymentFtlsToCreate(
                        partnerPayments.first().programmeLumpSumId,
                        partnerPayments.map { o ->
                            PaymentPartnerToCreate(
                                o.partnerId,
                                null,
                                o.amountApprovedPerPartner
                            )
                        },
                        partnerPayments.sumOf { it.amountApprovedPerPartner },
                        projectCustomIdentifier = projectOfCorrectVersion.customIdentifier,
                        projectAcronym = projectOfCorrectVersion.acronym,
                        defaultPartnerContribution = ftlsByFund[id.orderNr]!![id.programmeFundId]!!.partnerContribution,
                        defaultOfWhichPublic = ftlsByFund[id.orderNr]!![id.programmeFundId]!!.ofWhichPublic,
                        defaultOfWhichAutoPublic = ftlsByFund[id.orderNr]!![id.programmeFundId]!!.ofWhichAutoPublic,
                        defaultOfWhichPrivate = ftlsByFund[id.orderNr]!![id.programmeFundId]!!.ofWhichPrivate,
                    )
                }
            paymentPersistence.saveFTLSPayments(projectId, paymentsToUpdate)
            lumpSumOrderNrsToBeAdded.forEach { orderNr ->
                auditPublisher.publishEvent(monitoringFtlsReadyForPayment(this, projectOfCorrectVersion, orderNr, true))
            }
        }

        if (lumpSumOrderNrsToBeDeleted.isNotEmpty()) {
            paymentPersistence.deleteContributionsWhenReadyForPaymentReverted(projectId, lumpSumOrderNrsToBeDeleted)
            paymentPersistence.deleteFTLSByProjectIdAndOrderNrIn(projectId, lumpSumOrderNrsToBeDeleted)
            lumpSumOrderNrsToBeDeleted.forEach { orderNr ->
                auditPublisher.publishEvent(monitoringFtlsReadyForPayment(this, projectOfCorrectVersion, orderNr, false))
            }
        }
    }

}
