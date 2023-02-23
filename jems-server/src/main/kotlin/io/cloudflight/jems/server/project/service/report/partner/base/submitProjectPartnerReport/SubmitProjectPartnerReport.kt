package io.cloudflight.jems.server.project.service.report.partner.base.submitProjectPartnerReport

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.currency.repository.CurrencyPersistence
import io.cloudflight.jems.server.project.authorization.CanEditPartnerReport
import io.cloudflight.jems.server.project.repository.report.partner.model.ExpenditureVerificationUpdate
import io.cloudflight.jems.server.project.service.budget.model.ExpenditureCostCategoryCurrentlyReportedWithReIncluded
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.report.model.partner.ProjectPartnerReport
import io.cloudflight.jems.server.project.service.report.model.partner.ReportStatus
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ProjectPartnerReportExpenditureCost
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.coFinancing.ExpenditureCoFinancingCurrentWithReIncluded
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.investments.ExpenditureInvestmentCurrentWithReIncluded
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.lumpSum.ExpenditureLumpSumCurrentWithReIncluded
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.unitCost.ExpenditureUnitCostCurrentWithReIncluded
import io.cloudflight.jems.server.project.service.report.partner.ProjectPartnerReportPersistence
import io.cloudflight.jems.server.project.service.report.partner.base.runPreSubmissionCheck.RunPreSubmissionCheckService
import io.cloudflight.jems.server.project.service.report.partner.contribution.ProjectPartnerReportContributionPersistence
import io.cloudflight.jems.server.project.service.report.partner.contribution.extractOverview
import io.cloudflight.jems.server.project.service.report.partner.control.expenditure.ProjectPartnerReportExpenditureVerificationPersistence
import io.cloudflight.jems.server.project.service.report.partner.expenditure.ProjectPartnerReportExpenditurePersistence
import io.cloudflight.jems.server.project.service.report.partner.expenditure.fillCurrencyRates
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectPartnerReportExpenditureCoFinancingPersistence
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectPartnerReportExpenditureCostCategoryPersistence
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectPartnerReportInvestmentPersistence
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectPartnerReportLumpSumPersistence
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectPartnerReportUnitCostPersistence
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportCoFinancingBreakdown.generateCoFinCalculationInputData
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportCoFinancingBreakdown.getCurrentFrom
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportExpenditureBreakdown.calculateCurrent
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportExpenditureBreakdown.onlyReIncluded
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportExpenditureInvestementsBreakdown.getCurrentForInvestments
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportExpenditureLumpSumBreakdown.getCurrentForLumpSums
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportExpenditureUnitCostBreakdown.getCurrentForUnitCosts
import io.cloudflight.jems.server.project.service.report.partner.partnerReportSubmitted
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDate
import java.time.ZonedDateTime

@Service
class SubmitProjectPartnerReport(
    private val reportPersistence: ProjectPartnerReportPersistence,
    private val preSubmissionCheck: RunPreSubmissionCheckService,
    private val reportExpenditurePersistence: ProjectPartnerReportExpenditurePersistence,
    private val currencyPersistence: CurrencyPersistence,
    private val partnerPersistence: PartnerPersistence,
    private val reportExpenditureCostCategoryPersistence: ProjectPartnerReportExpenditureCostCategoryPersistence,
    private val reportExpenditureCoFinancingPersistence: ProjectPartnerReportExpenditureCoFinancingPersistence,
    private val reportContributionPersistence: ProjectPartnerReportContributionPersistence,
    private val reportLumpSumPersistence: ProjectPartnerReportLumpSumPersistence,
    private val reportUnitCostPersistence: ProjectPartnerReportUnitCostPersistence,
    private val reportInvestmentPersistence: ProjectPartnerReportInvestmentPersistence,
    private val projectControlReportExpenditurePersistence: ProjectPartnerReportExpenditureVerificationPersistence,
    private val auditPublisher: ApplicationEventPublisher,
) : SubmitProjectPartnerReportInteractor {

    @CanEditPartnerReport
    @Transactional
    @ExceptionWrapper(SubmitProjectPartnerReportException::class)
    override fun submit(partnerId: Long, reportId: Long): ReportStatus {
        val report = reportPersistence.getPartnerReportById(partnerId = partnerId, reportId = reportId)
        validateReportIsStillDraft(report)

        if (!preSubmissionCheck.preCheck(partnerId, reportId = reportId).isSubmissionAllowed) {
            throw SubmissionNotAllowed()
        }

        val expenditures = fillInVerificationForExpendituresAndSaveCurrencyRates(partnerId = partnerId, reportId = reportId)
        val costCategories = reportExpenditureCostCategoryPersistence.getCostCategories(partnerId = partnerId, reportId)

        val currentCostCategories = ExpenditureCostCategoryCurrentlyReportedWithReIncluded(
            currentlyReported = expenditures.calculateCurrent(options = costCategories.options),
            currentlyReportedReIncluded = expenditures.onlyReIncluded().calculateCurrent(options = costCategories.options),
        )

        saveCurrentCostCategories(currentCostCategories, partnerId = partnerId, reportId) // table 2
        saveCurrentCoFinancing( // table 1
            currentReport = currentCostCategories.currentlyReported.sum,
            currentReportReIncluded = currentCostCategories.currentlyReportedReIncluded.sum,
            totalEligibleBudget = costCategories.totalsFromAF.sum,
            report = report, partnerId = partnerId,
        )
        saveCurrentLumpSums(expenditures.getCurrentForLumpSums(), partnerId = partnerId, reportId) // table 3
        saveCurrentUnitCosts(expenditures.getCurrentForUnitCosts(), partnerId = partnerId, reportId) // table 4
        saveCurrentInvestments(expenditures.getCurrentForInvestments(), partnerId = partnerId, reportId) // table 5

        return reportPersistence.submitReportById(
            partnerId = partnerId,
            reportId = reportId,
            submissionTime = ZonedDateTime.now()
        ).also {
            auditPublisher.publishEvent(
                partnerReportSubmitted(
                    context = this,
                    projectId = partnerPersistence.getProjectIdForPartnerId(id = partnerId, it.version),
                    report = it,
                )
            )
        }.status
    }

    private fun validateReportIsStillDraft(report: ProjectPartnerReport) {
        if (report.status.isClosed())
            throw ReportAlreadyClosed()
    }

    private fun fillInVerificationForExpendituresAndSaveCurrencyRates(partnerId: Long, reportId: Long): List<ProjectPartnerReportExpenditureCost> {
        val expenditures = reportExpenditurePersistence.getPartnerReportExpenditureCosts(partnerId, reportId = reportId)
        val usedCurrencies = expenditures.mapTo(HashSet()) { it.currencyCode }

        val today = LocalDate.now()
        val rates = currencyPersistence.findAllByIdYearAndIdMonth(year = today.year, month = today.monthValue)
            .associateBy { it.code }
            .filterKeys { it in usedCurrencies }

        val notExistingRates = usedCurrencies.minus(rates.keys)
        if (notExistingRates.isNotEmpty())
            throw CurrencyRatesMissing(notExistingRates)

        val updatedExpenditures = reportExpenditurePersistence.updatePartnerReportExpenditureCosts(
            partnerId = partnerId,
            reportId = reportId,
            expenditureCosts = expenditures.fillCurrencyRates(rates),
            doNotRenumber = true,
        )

        projectControlReportExpenditurePersistence.updatePartnerControlReportExpenditureVerification(
            partnerId = partnerId,
            reportId = reportId,
            expenditureVerification = updatedExpenditures.emptyVerification(),
        )

        return updatedExpenditures
    }

    private fun saveCurrentCostCategories(
        currentCostCategoriesWithReIncluded: ExpenditureCostCategoryCurrentlyReportedWithReIncluded,
        partnerId: Long,
        reportId: Long
    ) {
        reportExpenditureCostCategoryPersistence.updateCurrentlyReportedValues(
            partnerId = partnerId,
            reportId = reportId,
            currentlyReportedWithReIncluded = currentCostCategoriesWithReIncluded,
        )
    }

    private fun saveCurrentCoFinancing(
        currentReport: BigDecimal,
        currentReportReIncluded: BigDecimal,
        totalEligibleBudget: BigDecimal,
        report: ProjectPartnerReport,
        partnerId: Long,
    ) {
        val contributions = reportContributionPersistence
            .getPartnerReportContribution(partnerId, reportId = report.id).extractOverview()

        reportExpenditureCoFinancingPersistence.updateCurrentlyReportedValues(
            partnerId = partnerId,
            reportId = report.id,
            currentlyReported = ExpenditureCoFinancingCurrentWithReIncluded(
                current = getCurrentFrom(
                    contributions.generateCoFinCalculationInputData(
                        totalEligibleBudget = totalEligibleBudget,
                        currentValueToSplit = currentReport,
                        funds = report.identification.coFinancing,
                    )
                ),
                currentReIncluded = getCurrentFrom(
                    contributions.generateCoFinCalculationInputData(
                        totalEligibleBudget = totalEligibleBudget,
                        currentValueToSplit = currentReportReIncluded,
                        funds = report.identification.coFinancing,
                    )
                )
            )
        )
    }

    private fun saveCurrentLumpSums(currentLumpSums: Map<Long, ExpenditureLumpSumCurrentWithReIncluded>, partnerId: Long, reportId: Long) {
        reportLumpSumPersistence.updateCurrentlyReportedValues(
            partnerId = partnerId,
            reportId = reportId,
            currentlyReported = currentLumpSums,
        )
    }

    private fun saveCurrentUnitCosts(currentUnitCosts: Map<Long, ExpenditureUnitCostCurrentWithReIncluded>, partnerId: Long, reportId: Long) {
        reportUnitCostPersistence.updateCurrentlyReportedValues(
            partnerId = partnerId,
            reportId = reportId,
            currentlyReported = currentUnitCosts,
        )
    }

    private fun saveCurrentInvestments(currentInvestments: Map<Long, ExpenditureInvestmentCurrentWithReIncluded>, partnerId: Long, reportId: Long) {
        reportInvestmentPersistence.updateCurrentlyReportedValues(
            partnerId = partnerId,
            reportId = reportId,
            currentlyReported = currentInvestments,
        )
    }

    private fun List<ProjectPartnerReportExpenditureCost>.emptyVerification() = map {
        ExpenditureVerificationUpdate(
            id = it.id!!,
            partOfSample = false,
            certifiedAmount = it.declaredAmountAfterSubmission ?: BigDecimal.ZERO,
            deductedAmount = BigDecimal.ZERO,
            typologyOfErrorId = null,
            parked = false,
            verificationComment = null
        )
    }

}
