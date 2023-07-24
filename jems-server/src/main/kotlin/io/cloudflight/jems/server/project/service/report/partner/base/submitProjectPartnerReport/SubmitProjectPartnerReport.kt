package io.cloudflight.jems.server.project.service.report.partner.base.submitProjectPartnerReport

import io.cloudflight.jems.plugin.contract.pre_condition_check.ControlReportSamplingCheckPlugin
import io.cloudflight.jems.server.call.service.CallPersistence
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.currency.repository.CurrencyPersistence
import io.cloudflight.jems.server.notification.handler.PartnerReportStatusChanged
import io.cloudflight.jems.server.plugin.JemsPluginRegistry
import io.cloudflight.jems.server.project.authorization.CanEditPartnerReport
import io.cloudflight.jems.server.project.service.ProjectPersistence
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
import io.cloudflight.jems.server.project.service.report.partner.base.runPartnerReportPreSubmissionCheck.RunPartnerReportPreSubmissionCheckService
import io.cloudflight.jems.server.project.service.report.partner.contribution.ProjectPartnerReportContributionPersistence
import io.cloudflight.jems.server.project.service.report.partner.contribution.extractOverview
import io.cloudflight.jems.server.project.service.report.partner.control.expenditure.ProjectPartnerReportExpenditureVerificationPersistence
import io.cloudflight.jems.server.project.service.report.partner.expenditure.ProjectPartnerReportExpenditurePersistence
import io.cloudflight.jems.server.project.service.report.partner.expenditure.fillCurrencyRates
import io.cloudflight.jems.server.project.service.report.partner.expenditure.toChanges
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectPartnerReportExpenditureCostCategoryPersistence
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectPartnerReportExpenditureCoFinancingPersistence
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectPartnerReportLumpSumPersistence
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectPartnerReportUnitCostPersistence
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectPartnerReportInvestmentPersistence
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
    private val preSubmissionCheck: RunPartnerReportPreSubmissionCheckService,
    private val reportExpenditurePersistence: ProjectPartnerReportExpenditurePersistence,
    private val currencyPersistence: CurrencyPersistence,
    private val partnerPersistence: PartnerPersistence,
    private val reportExpenditureCostCategoryPersistence: ProjectPartnerReportExpenditureCostCategoryPersistence,
    private val reportExpenditureCoFinancingPersistence: ProjectPartnerReportExpenditureCoFinancingPersistence,
    private val reportContributionPersistence: ProjectPartnerReportContributionPersistence,
    private val reportLumpSumPersistence: ProjectPartnerReportLumpSumPersistence,
    private val reportUnitCostPersistence: ProjectPartnerReportUnitCostPersistence,
    private val reportInvestmentPersistence: ProjectPartnerReportInvestmentPersistence,
    private val reportExpenditureVerificationPersistence: ProjectPartnerReportExpenditureVerificationPersistence,
    private val auditPublisher: ApplicationEventPublisher,
    private val projectPersistence: ProjectPersistence,
    private val jemsPluginRegistry: JemsPluginRegistry,
    private val callPersistence: CallPersistence,
) : SubmitProjectPartnerReportInteractor {

    @CanEditPartnerReport
    @Transactional
    @ExceptionWrapper(SubmitProjectPartnerReportException::class)
    override fun submit(partnerId: Long, reportId: Long): ReportStatus {
        val report = reportPersistence.getPartnerReportById(partnerId = partnerId, reportId = reportId)
        validateReportIsStillOpen(report)

        if (!preSubmissionCheck.preCheck(partnerId, reportId = reportId).isSubmissionAllowed) {
            throw SubmissionNotAllowed()
        }

        val expenditures = fillInVerificationForExpendituresAndSaveCurrencyRates(partnerId, reportId = reportId)
        val needsRecalculation = report.status.isOpenForNumbersChanges()
        if (needsRecalculation)
            storeCurrentValues(partnerId, report, expenditures)

        if (report.status == ReportStatus.ReOpenInControlLast) {
            // perform auto-sampling
            reportExpenditurePersistence.markAsSampledAndLock(
                expenditureIds = getSampledExpenditureIdsFromPlugin(partnerId, reportId = reportId)
            )
        }

        return reportPersistence.updateStatusAndTimes(partnerId, reportId = reportId, status = report.status.submitStatus(report.hasControlReopenedBefore()),
            firstSubmissionTime = if (report.status.isOpenInitially()) ZonedDateTime.now() else null /* no update */,
            lastReSubmissionTime = if (!report.status.isOpenInitially()) ZonedDateTime.now() else null /* no update */,
        ).also { partnerReportSummary ->
            val projectId = partnerPersistence.getProjectIdForPartnerId(id = partnerId, partnerReportSummary.version)
            val projectSummary = projectPersistence.getProjectSummary(projectId)

            auditPublisher.publishEvent(PartnerReportStatusChanged(this, projectSummary, partnerReportSummary, report.status))
            auditPublisher.publishEvent(
                partnerReportSubmitted(
                    context = this,
                    projectId = projectId,
                    report = partnerReportSummary,
                    isGdprSensitive = expenditures.any { it.gdpr }
                )
            )
        }.status
    }

    private fun ProjectPartnerReport.hasControlReopenedBefore() = this.lastControlReopening != null

    private fun validateReportIsStillOpen(report: ProjectPartnerReport) {
        if (report.status.isClosed())
            throw ReportAlreadyClosed()
    }

    private fun storeCurrentValues(partnerId: Long, report: ProjectPartnerReport, expenditures: List<ProjectPartnerReportExpenditureCost>) {
        val costCategories = reportExpenditureCostCategoryPersistence.getCostCategories(partnerId = partnerId, report.id)

        val currentCostCategories = ExpenditureCostCategoryCurrentlyReportedWithReIncluded(
            currentlyReported = expenditures.calculateCurrent(options = costCategories.options),
            currentlyReportedReIncluded = expenditures.onlyReIncluded().calculateCurrent(options = costCategories.options),
        )

        saveCurrentCostCategories(currentCostCategories, partnerId = partnerId, report.id) // table 2
        saveCurrentCoFinancing( // table 1
            currentReport = currentCostCategories.currentlyReported.sum,
            currentReportReIncluded = currentCostCategories.currentlyReportedReIncluded.sum,
            totalEligibleBudget = costCategories.totalsFromAF.sum,
            report = report, partnerId = partnerId,
        )
        saveCurrentLumpSums(expenditures.getCurrentForLumpSums(), partnerId = partnerId, report.id) // table 3
        saveCurrentUnitCosts(expenditures.getCurrentForUnitCosts(), partnerId = partnerId, report.id) // table 4
        saveCurrentInvestments(expenditures.getCurrentForInvestments(), partnerId = partnerId, report.id) // table 5
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

        return reportExpenditureVerificationPersistence.updateExpenditureCurrencyRatesAndClearVerification(
            partnerId = partnerId,
            reportId = reportId,
            newRates = expenditures.fillCurrencyRates(rates).toChanges(),
        )
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

    private fun getSampledExpenditureIdsFromPlugin(partnerId: Long, reportId: Long): Set<Long> {
        val pluginKey = callPersistence.getCallSimpleByPartnerId(partnerId).controlReportSamplingCheckPluginKey
        val plugin = jemsPluginRegistry.get(ControlReportSamplingCheckPlugin::class, key = pluginKey)
        return runCatching { plugin.check(partnerId = partnerId, reportId = reportId).sampledExpenditureIds }
            .getOrElse { setOf() }
    }

}
