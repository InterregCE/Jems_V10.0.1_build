package io.cloudflight.jems.server.project.service.report.partner.base.submitProjectPartnerReport

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.currency.repository.CurrencyPersistence
import io.cloudflight.jems.server.project.authorization.CanEditPartnerReport
import io.cloudflight.jems.server.project.service.budget.model.BudgetCostsCalculationResultFull
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.report.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.model.partner.ProjectPartnerReport
import io.cloudflight.jems.server.project.service.report.model.partner.ReportStatus
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ProjectPartnerReportExpenditureCost
import io.cloudflight.jems.server.project.service.report.partner.contribution.ProjectReportContributionPersistence
import io.cloudflight.jems.server.project.service.report.partner.contribution.extractOverview
import io.cloudflight.jems.server.project.service.report.partner.expenditure.ProjectReportExpenditurePersistence
import io.cloudflight.jems.server.project.service.report.partner.expenditure.fillCurrencyRates
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectReportExpenditureCoFinancingPersistence
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectReportExpenditureCostCategoryPersistence
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectReportInvestmentPersistence
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectReportLumpSumPersistence
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectReportUnitCostPersistence
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportCoFinancingBreakdown.generateCoFinCalculationInputData
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportCoFinancingBreakdown.getCurrentFrom
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportExpenditureBreakdown.calculateCurrent
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportExpenditureInvestementsBreakdown.getCurrentForInvestments
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportExpenditureLumpSumBreakdown.getCurrentForLumpSums
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportExpenditureUnitCostBreakdown.getCurrentForUnitCosts
import io.cloudflight.jems.server.project.service.report.partnerReportSubmitted
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDate
import java.time.ZonedDateTime

@Service
class SubmitProjectPartnerReport(
    private val reportPersistence: ProjectReportPersistence,
    private val reportExpenditurePersistence: ProjectReportExpenditurePersistence,
    private val currencyPersistence: CurrencyPersistence,
    private val partnerPersistence: PartnerPersistence,
    private val reportExpenditureCostCategoryPersistence: ProjectReportExpenditureCostCategoryPersistence,
    private val reportExpenditureCoFinancingPersistence: ProjectReportExpenditureCoFinancingPersistence,
    private val reportContributionPersistence: ProjectReportContributionPersistence,
    private val reportLumpSumPersistence: ProjectReportLumpSumPersistence,
    private val reportUnitCostPersistence: ProjectReportUnitCostPersistence,
    private val reportInvestmentPersistence: ProjectReportInvestmentPersistence,
    private val auditPublisher: ApplicationEventPublisher,
) : SubmitProjectPartnerReportInteractor {

    @CanEditPartnerReport
    @Transactional
    @ExceptionWrapper(SubmitProjectPartnerReportException::class)
    override fun submit(partnerId: Long, reportId: Long): ReportStatus {
        val report = reportPersistence.getPartnerReportById(partnerId = partnerId, reportId = reportId)
        validateReportIsStillDraft(report)

        val expenditures = validateExpendituresAndSaveCurrencyRates(partnerId = partnerId, reportId = reportId)
        val costCategories = reportExpenditureCostCategoryPersistence.getCostCategories(partnerId = partnerId, reportId)
        val currentCostCategories = expenditures.calculateCurrent(options = costCategories.options)

        saveCurrentCostCategories(currentCostCategories, partnerId = partnerId, reportId)
        saveCurrentCoFinancing(
            currentExpenditure = currentCostCategories.sum,
            totalEligibleBudget = costCategories.totalsFromAF.sum,
            report = report, partnerId = partnerId,
        )
        saveCurrentLumpSums(expenditures.getCurrentForLumpSums(), partnerId = partnerId, reportId)
        saveCurrentUnitCosts(expenditures.getCurrentForUnitCosts(), partnerId = partnerId, reportId)
        saveCurrentInvestments(expenditures.getCurrentForInvestments(), partnerId = partnerId, reportId)

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

    private fun validateExpendituresAndSaveCurrencyRates(partnerId: Long, reportId: Long): List<ProjectPartnerReportExpenditureCost> {
        val expenditures = reportExpenditurePersistence.getPartnerReportExpenditureCosts(partnerId, reportId = reportId)
        val usedCurrencies = expenditures.mapTo(HashSet()) { it.currencyCode }

        val today = LocalDate.now()
        val rates = currencyPersistence.findAllByIdYearAndIdMonth(year = today.year, month = today.monthValue)
            .associateBy { it.code }
            .filterKeys { it in usedCurrencies }

        val notExistingRates = usedCurrencies.minus(rates.keys)
        if (notExistingRates.isNotEmpty())
            throw CurrencyRatesMissing(notExistingRates)

        return reportExpenditurePersistence.updatePartnerReportExpenditureCosts(
            partnerId = partnerId,
            reportId = reportId,
            expenditureCosts = expenditures.fillCurrencyRates(rates),
        )
    }

    private fun saveCurrentCostCategories(currentCostCategories: BudgetCostsCalculationResultFull, partnerId: Long, reportId: Long) {
        reportExpenditureCostCategoryPersistence.updateCurrentlyReportedValues(
            partnerId = partnerId,
            reportId = reportId,
            currentlyReported = currentCostCategories,
        )
    }

    private fun saveCurrentCoFinancing(
        currentExpenditure: BigDecimal,
        totalEligibleBudget: BigDecimal,
        report: ProjectPartnerReport,
        partnerId: Long,
    ) {
        val contributions = reportContributionPersistence
            .getPartnerReportContribution(partnerId, reportId = report.id).extractOverview()

        reportExpenditureCoFinancingPersistence.updateCurrentlyReportedValues(
            partnerId = partnerId,
            reportId = report.id,
            currentlyReported = getCurrentFrom(
                contributions.generateCoFinCalculationInputData(
                    totalEligibleBudget = totalEligibleBudget,
                    currentValueToSplit = currentExpenditure,
                    funds = report.identification.coFinancing,
                )
            ),
        )
    }

    private fun saveCurrentLumpSums(currentLumpSums: Map<Long, BigDecimal>, partnerId: Long, reportId: Long) {
        reportLumpSumPersistence.updateCurrentlyReportedValues(
            partnerId = partnerId,
            reportId = reportId,
            currentlyReported = currentLumpSums,
        )
    }

    private fun saveCurrentUnitCosts(currentUnitCosts: Map<Long, BigDecimal>, partnerId: Long, reportId: Long) {
        reportUnitCostPersistence.updateCurrentlyReportedValues(
            partnerId = partnerId,
            reportId = reportId,
            currentlyReported = currentUnitCosts,
        )
    }

    private fun saveCurrentInvestments(currentInvestments: Map<Long, BigDecimal>, partnerId: Long, reportId: Long) {
        reportInvestmentPersistence.updateCurrentlyReportedValues(
            partnerId = partnerId,
            reportId = reportId,
            currentlyReported = currentInvestments,
        )
    }

}
