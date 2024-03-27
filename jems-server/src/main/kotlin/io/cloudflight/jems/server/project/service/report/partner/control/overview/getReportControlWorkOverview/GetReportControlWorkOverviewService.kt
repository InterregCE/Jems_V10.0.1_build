package io.cloudflight.jems.server.project.service.report.partner.control.overview.getReportControlWorkOverview

import io.cloudflight.jems.server.project.service.budget.model.BudgetCostsCurrentValuesWrapper
import io.cloudflight.jems.server.project.service.report.model.partner.control.overview.ControlWorkOverview
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.control.ProjectPartnerReportExpenditureVerification
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.costCategory.ReportExpenditureCostCategory
import io.cloudflight.jems.server.project.service.report.partner.ProjectPartnerReportPersistence
import io.cloudflight.jems.server.project.service.report.partner.control.expenditure.ProjectPartnerReportExpenditureVerificationPersistence
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectPartnerReportExpenditureCostCategoryPersistence
import io.cloudflight.jems.server.project.service.report.percentageOf
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
class GetReportControlWorkOverviewService(
    private val reportPersistence: ProjectPartnerReportPersistence,
    private val reportControlExpenditurePersistence: ProjectPartnerReportExpenditureVerificationPersistence,
    private val reportExpenditureCostCategoryPersistence: ProjectPartnerReportExpenditureCostCategoryPersistence,
) {

    @Transactional(readOnly = true)
    fun get(partnerId: Long, reportId: Long): ControlWorkOverview {
        val isClosed = reportPersistence.getPartnerReportById(partnerId = partnerId, reportId = reportId).status.isFinalized()

        val currentExpenditures = reportControlExpenditurePersistence
            .getPartnerControlReportExpenditureVerification(partnerId, reportId = reportId)

        val costCategories = reportExpenditureCostCategoryPersistence.getCostCategories(partnerId, reportId = reportId)
        val currentReport = costCategories.currentlyReported.sum

        val parkedAndEligibleAfterControl = if (isClosed)
            costCategories.getParkedAndEligibleAfterControl()
        else
            getParkedAndEligibleAfterControl(currentExpenditures, costCategories).toSimple()

        val currentReportFlatRates = costCategories.currentlyReported.extractFlatRatesSum(costCategories.options)
        val currentReportWithoutFlatRates = currentReport.minus(currentReportFlatRates)
        val controlSample = currentExpenditures.onlySamplingOnes().sum()
        val parked = parkedAndEligibleAfterControl.parked
        val eligibleAfterControl = parkedAndEligibleAfterControl.eligibleAfterControl

        return ControlWorkOverview(
            declaredByPartner = currentReport,
            declaredByPartnerFlatRateSum = currentReportFlatRates,
            inControlSample = controlSample,
            inControlSamplePercentage = controlSample.percentageOf(currentReportWithoutFlatRates) ?: BigDecimal.ZERO,
            parked = parked,
            deductedByControl = currentReport.minus(eligibleAfterControl).minus(parked),
            eligibleAfterControl = eligibleAfterControl,
            eligibleAfterControlPercentage = eligibleAfterControl.percentageOf(currentReport) ?: BigDecimal.ZERO,
        )
    }

    private fun Collection<BigDecimal?>.sum() = sumOf { it ?: BigDecimal.ZERO }

    private fun Collection<ProjectPartnerReportExpenditureVerification>.onlySamplingOnes() =
        filter { it.partOfSample }.map { it.declaredAmountAfterSubmission }

    private fun ReportExpenditureCostCategory.getParkedAndEligibleAfterControl() = ParkedAndEligibleAfterControl(
        eligibleAfterControl = totalEligibleAfterControl.sum,
        parked = currentlyReportedParked.sum,
    )

    private fun BudgetCostsCurrentValuesWrapper.toSimple() = ParkedAndEligibleAfterControl(
        eligibleAfterControl = currentlyReported.sum,
        parked = currentlyReportedParked.sum,
    )

}
