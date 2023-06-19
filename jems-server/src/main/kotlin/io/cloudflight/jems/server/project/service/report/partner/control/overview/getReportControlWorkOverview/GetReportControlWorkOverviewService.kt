package io.cloudflight.jems.server.project.service.report.partner.control.overview.getReportControlWorkOverview

import io.cloudflight.jems.server.project.service.budget.model.BudgetCostsCalculationResultFull
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerBudgetOptions
import io.cloudflight.jems.server.project.service.report.model.partner.control.overview.ControlWorkOverview
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.control.ProjectPartnerReportExpenditureVerification
import io.cloudflight.jems.server.project.service.report.partner.ProjectPartnerReportPersistence
import io.cloudflight.jems.server.project.service.report.partner.control.expenditure.ProjectPartnerReportExpenditureVerificationPersistence
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectPartnerReportExpenditureCostCategoryPersistence
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportExpenditureBreakdown.calculateCurrent
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

        val controlSample = currentExpenditures.onlySamplingOnes().sum()

        val parkedSum = if (isClosed)
            costCategories.currentlyReportedParked.sum
        else
            currentExpenditures.onlyParkedOnes().calculateCurrent(costCategories.options).sum

        val eligibleAfterControl = if (isClosed)
            costCategories.totalEligibleAfterControl.sum
        else
            currentExpenditures.calculateCertified(costCategories.options).sum

        val currentReportSum = costCategories.currentlyReported.sum
        val currentReportSumWithoutFlatRates = costCategories.currentlyReported.sumIgnoringFlatRates(costCategories.options)

        return ControlWorkOverview(
            declaredByPartner = currentReportSum,
            declaredByPartnerFlatRateSum = costCategories.currentlyReported.flatRatesSum(costCategories.options),
            inControlSample = controlSample,
            inControlSamplePercentage = controlSample.percentageOf(currentReportSumWithoutFlatRates) ?: BigDecimal.ZERO,
            parked = parkedSum,
            deductedByControl = currentReportSum.minus(eligibleAfterControl).minus(parkedSum),
            eligibleAfterControl = eligibleAfterControl,
            eligibleAfterControlPercentage = eligibleAfterControl.percentageOf(currentReportSum) ?: BigDecimal.ZERO,
        )
    }

    private fun Collection<BigDecimal?>.sum() = sumOf { it ?: BigDecimal.ZERO }

    private fun Collection<ProjectPartnerReportExpenditureVerification>.onlySamplingOnes() =
        filter { it.partOfSample }.map { it.declaredAmountAfterSubmission }

    private fun BudgetCostsCalculationResultFull.sumIgnoringFlatRates(options: ProjectPartnerBudgetOptions) =
        sum.minus(this.flatRatesSum(options))

    private fun BudgetCostsCalculationResultFull.flatRatesSum(options: ProjectPartnerBudgetOptions): BigDecimal {
        var flatRatesSum = BigDecimal.ZERO
        with(options) {
            if (hasFlatRateOffice()) { flatRatesSum += office }
            if (hasFlatRateTravel()) { flatRatesSum += travel }
            if (hasFlatRateStaff()) { flatRatesSum += staff }
            if (hasFlatRateOther()) { flatRatesSum += other }
        }
        return flatRatesSum
    }

    private fun ProjectPartnerBudgetOptions.hasFlatRateOffice() =
        officeAndAdministrationOnDirectCostsFlatRate != null || officeAndAdministrationOnStaffCostsFlatRate != null
    private fun ProjectPartnerBudgetOptions.hasFlatRateTravel() = travelAndAccommodationOnStaffCostsFlatRate != null
    private fun ProjectPartnerBudgetOptions.hasFlatRateStaff() = staffCostsFlatRate != null
    private fun ProjectPartnerBudgetOptions.hasFlatRateOther() = otherCostsOnStaffCostsFlatRate != null
}
