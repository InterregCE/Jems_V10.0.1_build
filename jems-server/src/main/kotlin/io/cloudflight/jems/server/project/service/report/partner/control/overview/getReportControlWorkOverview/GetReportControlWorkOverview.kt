package io.cloudflight.jems.server.project.service.report.partner.control.overview.getReportControlWorkOverview

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanViewPartnerControlReport
import io.cloudflight.jems.server.project.service.budget.calculator.calculateBudget
import io.cloudflight.jems.server.project.service.budget.model.BudgetCostsCalculationResultFull
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerBudgetOptions
import io.cloudflight.jems.server.project.service.report.model.partner.control.overview.ControlWorkOverview
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.control.ProjectPartnerReportExpenditureVerification
import io.cloudflight.jems.server.project.service.report.partner.expenditure.control.ProjectReportControlExpenditurePersistence
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectReportExpenditureCoFinancingPersistence
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectReportExpenditureCostCategoryPersistence
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportExpenditureBreakdown.getCategory
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportExpenditureBreakdown.percentageOf
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
class GetReportControlWorkOverview(
    private val reportCoFinancingPersistence: ProjectReportExpenditureCoFinancingPersistence,
    private val reportControlExpenditurePersistence: ProjectReportControlExpenditurePersistence,
    private val reportExpenditureCostCategoryPersistence: ProjectReportExpenditureCostCategoryPersistence,
) : GetReportControlWorkOverviewInteractor {

    @CanViewPartnerControlReport
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetReportControlWorkOverviewException::class)
    override fun get(partnerId: Long, reportId: Long): ControlWorkOverview {
        val currentExpenditures = reportControlExpenditurePersistence
            .getPartnerControlReportExpenditureVerification(partnerId, reportId = reportId)

        val costCategories = reportExpenditureCostCategoryPersistence.getCostCategories(partnerId, reportId = reportId)

        val controlSample = currentExpenditures.onlySamplingOnes().sum()
        val eligibleAfterControl = currentExpenditures.calculateCertified(costCategories.options).sum

        val currentReportSum = reportCoFinancingPersistence.getReportCurrentSum(partnerId, reportId = reportId)

        return ControlWorkOverview(
            declaredByPartner = currentReportSum,
            inControlSample = controlSample,
            parked = BigDecimal.ZERO,
            deductedByControl = currentReportSum.minus(eligibleAfterControl),
            eligibleAfterControl = eligibleAfterControl,
            eligibleAfterControlPercentage = eligibleAfterControl.percentageOf(currentReportSum),
        )
    }

    private fun Collection<BigDecimal?>.sum() = sumOf { it ?: BigDecimal.ZERO }

    private fun Collection<ProjectPartnerReportExpenditureVerification>.onlySamplingOnes() =
        filter { it.partOfSample }.map { it.declaredAmountAfterSubmission }

    private fun Collection<ProjectPartnerReportExpenditureVerification>.calculateCertified(
        options: ProjectPartnerBudgetOptions
    ): BudgetCostsCalculationResultFull {
        val sums = groupBy { it.getCategory() }
            .mapValues { it.value.sumOf { it.certifiedAmount } }
        return calculateBudget(options, sums)
    }

}
