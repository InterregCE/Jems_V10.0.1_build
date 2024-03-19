package io.cloudflight.jems.server.project.service.contracting.monitoring.updateProjectContractingMonitoring

import io.cloudflight.jems.server.common.audit.fromOldToNewChanges
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.payments.entity.PaymentGroupingId
import io.cloudflight.jems.server.payments.model.regular.PaymentPerPartner
import io.cloudflight.jems.server.payments.model.regular.toCreate.PaymentFtlsToCreate
import io.cloudflight.jems.server.payments.model.regular.toCreate.PaymentPartnerToCreate
import io.cloudflight.jems.server.payments.repository.applicationToEc.linkToPayment.PaymentApplicationToEcLinkPersistenceProvider
import io.cloudflight.jems.server.payments.service.monitoringFtlsReadyForPayment
import io.cloudflight.jems.server.payments.service.regular.PaymentPersistence
import io.cloudflight.jems.server.project.authorization.CanSetProjectToContracted
import io.cloudflight.jems.server.project.repository.ProjectPersistenceProvider
import io.cloudflight.jems.server.project.service.ProjectVersionPersistence
import io.cloudflight.jems.server.project.service.budget.get_project_budget.GetProjectBudget
import io.cloudflight.jems.server.project.service.contracting.ContractingValidator
import io.cloudflight.jems.server.project.service.contracting.fillClosureLastPaymentDates
import io.cloudflight.jems.server.project.service.contracting.fillEndDateWithDuration
import io.cloudflight.jems.server.project.service.contracting.fillLumpSumsList
import io.cloudflight.jems.server.project.service.contracting.model.ProjectContractingMonitoring
import io.cloudflight.jems.server.project.service.contracting.monitoring.ContractingMonitoringPersistence
import io.cloudflight.jems.server.project.service.lumpsum.ProjectLumpSumPersistence
import io.cloudflight.jems.server.project.service.lumpsum.model.ProjectLumpSum
import io.cloudflight.jems.server.project.service.model.ProjectFull
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.partner.cofinancing.ProjectPartnerCoFinancingPersistence
import io.cloudflight.jems.server.project.service.projectContractingMonitoringChanged
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportCoFinancingBreakdown.DetailedSplit
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportCoFinancingBreakdown.generateCoFinCalculationInputData
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportCoFinancingBreakdown.getCurrentFrom
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.ZonedDateTime

@Service
class UpdateContractingMonitoring(
    private val contractingMonitoringPersistence: ContractingMonitoringPersistence,
    private val projectPersistence: ProjectPersistenceProvider,
    private val versionPersistence: ProjectVersionPersistence,
    private val projectLumpSumPersistence: ProjectLumpSumPersistence,
    private val partnerCoFinancingPersistence: ProjectPartnerCoFinancingPersistence,
    private val getProjectBudget: GetProjectBudget,
    private val partnerPersistence: PartnerPersistence,
    private val validator: ContractingValidator,
    private val auditPublisher: ApplicationEventPublisher,
    private val paymentPersistence: PaymentPersistence,
    private val paymentToEcPersistenceProvider: PaymentApplicationToEcLinkPersistenceProvider,
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
                .fillLumpSumsList(lumpSums = projectLumpSumPersistence.getLumpSums(projectId = projectId, version))
            val project = projectPersistence.getProject(projectId = projectId, version)
            val updated = contractingMonitoringPersistence.updateContractingMonitoring(
                contractMonitoring.copy(projectId = projectId, closureDate = oldMonitoring.closureDate)
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
            projectLumpSumPersistence.updateLumpSumsReadyForPayment(projectId, contractMonitoring.fastTrackLumpSums!!)
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

            val allPartners = partnerPersistence.findAllByProjectIdForDropdown(projectId, Sort.by(Sort.Order.asc("sortNumber")), version)
            return updated
                .fillClosureLastPaymentDates(allPartners, contractingMonitoringPersistence.getPartnerPaymentDate(projectId))
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
        val lumpSumsPaymentLinksToEcApplication = paymentToEcPersistenceProvider.getFtlsIdLinkToEcPaymentIdByProjectId(projectId)
        lumpSums.forEach {
            val lumpSum = savedFastTrackLumpSums.first { o -> o.orderNr == it.orderNr }
            it.lastApprovedVersionBeforeReadyForPayment = lumpSum.lastApprovedVersionBeforeReadyForPayment
            it.paymentEnabledDate = lumpSum.paymentEnabledDate
            if (lumpSum.readyForPayment != it.readyForPayment) {
                validateReadyFlagChangeIsAllowed(projectId, lumpSum, lumpSumsPaymentLinksToEcApplication)
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

    private fun validateReadyFlagChangeIsAllowed(
        projectId: Long,
        lumpSum: ProjectLumpSum,
        lumpSumsPaymentLinksToEcApplication: Map<Int, Long>
    ) {
        if (contractingMonitoringPersistence.existsSavedInstallment(projectId, lumpSum.programmeLumpSumId, lumpSum.orderNr)) {
            throw UpdateContractingMonitoringFTLSHasInstallmentsException()
        }

        if (lumpSumsPaymentLinksToEcApplication[lumpSum.orderNr] != null) {
            throw UpdateContractingMonitoringFTLSLinkedToEcPaymentException()
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
            .associate { Pair(it.partner.id!!, it.totalBudgetWithoutSpf()) }

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
            val paymentsToUpdate = paymentPersistence
                .getAmountPerPartnerByProjectIdAndLumpSumOrderNrIn(projectId, lumpSumOrderNrsToBeAdded)
                .groupBy { PaymentGroupingId(it.orderNr, it.programmeFundId) }
                .mapValues { (id, partnerPayments) ->
                    val totalEligible = ftlsByFund.getFundValue(id).value
                        .add(ftlsByFund.getFundValue(id).partnerContribution)
                    PaymentFtlsToCreate(
                        partnerPayments.first().programmeLumpSumId,
                        partnerPayments.toPartnerPaymentsToCreate(version),
                        partnerPayments.sumOf { it.amountApprovedPerPartner },
                        projectCustomIdentifier = projectOfCorrectVersion.customIdentifier,
                        projectAcronym = projectOfCorrectVersion.acronym,
                        defaultTotalEligibleWithoutSco = totalEligible,
                        defaultFundAmountUnionContribution = BigDecimal.ZERO,
                        defaultFundAmountPublicContribution = ftlsByFund.getFundValue(id).value,
                        defaultPartnerContribution = ftlsByFund.getFundValue(id).partnerContribution,
                        defaultOfWhichPublic = ftlsByFund.getFundValue(id).ofWhichPublic,
                        defaultOfWhichAutoPublic = ftlsByFund.getFundValue(id).ofWhichAutoPublic,
                        defaultOfWhichPrivate = ftlsByFund.getFundValue(id).ofWhichPrivate,
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

    private fun List<PaymentPerPartner>.toPartnerPaymentsToCreate(version: String) =
        this.map { ppp ->
            val partner = partnerPersistence.getById(ppp.partnerId, version)
            return@map PaymentPartnerToCreate(
                partnerId = ppp.partnerId,
                partnerReportId = null,
                amountApprovedPerPartner = ppp.amountApprovedPerPartner,
                partnerAbbreviationIfFtls = partner.abbreviation,
                partnerNameInOriginalLanguageIfFtls = partner.nameInOriginalLanguage ?: "",
                partnerNameInEnglishIfFtls = partner.nameInEnglish ?: "",
            )
        }

    private fun Map<Int, Map<Long?, DetailedSplit>>.getFundValue(id: PaymentGroupingId) =
        this[id.orderNr]!![id.programmeFundId]!!
}


