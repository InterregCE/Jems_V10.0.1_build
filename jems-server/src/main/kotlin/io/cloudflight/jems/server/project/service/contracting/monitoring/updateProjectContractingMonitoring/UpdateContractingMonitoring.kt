package io.cloudflight.jems.server.project.service.contracting.monitoring.updateProjectContractingMonitoring

import io.cloudflight.jems.server.common.audit.fromOldToNewChanges
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.payments.entity.PaymentGroupingId
import io.cloudflight.jems.server.payments.model.regular.PaymentPartnerToCreate
import io.cloudflight.jems.server.payments.model.regular.PaymentToCreate
import io.cloudflight.jems.server.payments.model.regular.contributionMeta.ContributionMeta
import io.cloudflight.jems.server.payments.service.monitoringFtlsReadyForPayment
import io.cloudflight.jems.server.payments.service.regular.PaymentRegularPersistence
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
import io.cloudflight.jems.server.project.service.model.ProjectSummary
import io.cloudflight.jems.server.project.service.partner.cofinancing.ProjectPartnerCoFinancingPersistence
import io.cloudflight.jems.server.project.service.projectContractingMonitoringChanged
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.coFinancing.ReportExpenditureCoFinancingColumn
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
    private val paymentPersistence: PaymentRegularPersistence,
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

            val lumpSums = contractMonitoring.fastTrackLumpSums!!
            updateReadyForPayment(
                projectId = projectId,
                lumpSums = lumpSums,
                savedFastTrackLumpSums = oldMonitoring.fastTrackLumpSums!!,
                orderNrsToBeAdded = lumpSumsOrderNrTobeAdded,
                orderNrsToBeDeleted = lumpSumsOrderNrToBeDeleted
            )
            projectLumpSumPersistence.updateLumpSums(projectId, contractMonitoring.fastTrackLumpSums!!)
            updateApprovedAmountPerPartner(projectSummary, lumpSumsOrderNrTobeAdded, lumpSumsOrderNrToBeDeleted,
                projectCustomIdentifier = project.customIdentifier, projectAcronym = project.acronym)
            updateApprovedAmountContributions(
                projectId = projectId,
                lumpSumsToUpdate = lumpSums.filter { it.orderNr in lumpSumsOrderNrTobeAdded },
                orderNrsToBeDeleted = lumpSumsOrderNrToBeDeleted,
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

    private fun updateApprovedAmountPerPartner(
        project: ProjectSummary,
        orderNrsToBeAdded: MutableSet<Int>,
        orderNrsToBeDeleted: Set<Int>,
        projectCustomIdentifier: String,
        projectAcronym: String,
    ) {
        val projectId = project.id
        if (orderNrsToBeDeleted.isNotEmpty()) {
            this.paymentPersistence.deleteAllByProjectIdAndOrderNrIn(projectId, orderNrsToBeDeleted)
            orderNrsToBeDeleted.forEach { orderNr ->
                auditPublisher.publishEvent(monitoringFtlsReadyForPayment(this, project, orderNr, false))
            }
        }

        if (orderNrsToBeAdded.isNotEmpty()) {
            val calculatedAmountsToBeAdded = this.paymentPersistence.getAmountPerPartnerByProjectIdAndLumpSumOrderNrIn(projectId, orderNrsToBeAdded)

            val paymentsToUpdate = calculatedAmountsToBeAdded.groupBy { PaymentGroupingId(it.orderNr, it.programmeFundId) }
                .mapValues { (_, partnerPayments) ->
                    PaymentToCreate(
                        partnerPayments.first().programmeLumpSumId,
                        partnerPayments.map { o ->
                            PaymentPartnerToCreate(
                                o.partnerId,
                                o.amountApprovedPerPartner
                            )
                        },
                        partnerPayments.sumOf { it.amountApprovedPerPartner },
                        projectCustomIdentifier = projectCustomIdentifier,
                        projectAcronym = projectAcronym,
                    )
                }

            this.paymentPersistence.savePaymentToProjects(projectId, paymentsToUpdate)
            orderNrsToBeAdded.forEach { orderNr ->
                auditPublisher.publishEvent(monitoringFtlsReadyForPayment(this, project, orderNr, true))
            }
        }
    }

    private fun updateApprovedAmountContributions(
        projectId: Long,
        lumpSumsToUpdate: List<ProjectLumpSum>,
        orderNrsToBeDeleted: Set<Int>,
        version: String,
    ) {
        val partnersById = getProjectBudget.getBudget(projectId = projectId, version).associateBy { it.partner.id!! }
        val paymentContributions = lumpSumsToUpdate.map { lumpSum ->
            lumpSum.lumpSumContributions.map { contribution ->
                getCurrentFrom(
                    generateCoFinCalculationInputData(
                        totalEligibleBudget = partnersById[contribution.partnerId]!!.totalCosts,
                        currentValueToSplit = contribution.amount,
                        coFinancing = partnerCoFinancingPersistence
                            .getCoFinancingAndContributions(contribution.partnerId, version),
                    )
                ).toPaymentContributionModel(
                    projectId = projectId,
                    partnerId = contribution.partnerId,
                    lumpSum = lumpSum,
                )
            }
        }.flatten()
        if (paymentContributions.isNotEmpty())
            paymentPersistence.storePartnerContributionsWhenReadyForPayment(paymentContributions)
        if (orderNrsToBeDeleted.isNotEmpty())
            paymentPersistence.deleteContributionsWhenReadyForPaymentReverted(projectId, orderNrsToBeDeleted)
    }

    private fun ReportExpenditureCoFinancingColumn.toPaymentContributionModel(
        projectId: Long,
        partnerId: Long,
        lumpSum: ProjectLumpSum,
    ) = ContributionMeta(
        projectId = projectId,
        partnerId = partnerId,
        programmeLumpSumId = lumpSum.programmeLumpSumId,
        orderNr = lumpSum.orderNr,
        partnerContribution = partnerContribution,
        publicContribution = publicContribution,
        automaticPublicContribution = automaticPublicContribution,
        privateContribution = privateContribution,
    )
}
