package io.cloudflight.jems.server.project.service.report.partner.financialOverview

import io.cloudflight.jems.server.project.service.budget.model.BudgetCostsCalculationResultFull
import io.cloudflight.jems.server.project.service.budget.model.BudgetCostsCurrentValuesWrapper
import io.cloudflight.jems.server.project.service.budget.model.ExpenditureCostCategoryCurrentlyReportedWithReIncluded
import io.cloudflight.jems.server.project.service.budget.model.ExpenditureCostCategoryPreviouslyReportedWithParked
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.costCategory.ReportExpenditureCostCategory

interface ProjectPartnerReportExpenditureCostCategoryPersistence {

    fun getCostCategories(partnerId: Long, reportId: Long): ReportExpenditureCostCategory

    fun getCostCategoriesFor(reportIds: Set<Long>): Map<Long, ReportExpenditureCostCategory>

    fun getCostCategoriesCumulative(reportIds: Set<Long>, finalizedReportIds: Set<Long>): ExpenditureCostCategoryPreviouslyReportedWithParked

    fun getVerificationCostCategoriesCumulative(partnerId: Long, finalizedProjectReportIds: Set<Long>): BudgetCostsCalculationResultFull

    fun updateCurrentlyReportedValues(
        partnerId: Long,
        reportId: Long,
        currentlyReportedWithReIncluded: ExpenditureCostCategoryCurrentlyReportedWithReIncluded
    )

    fun updateAfterControlValues(
        partnerId: Long,
        reportId: Long,
        afterControlWithParked: BudgetCostsCurrentValuesWrapper,
    )

    fun updateAfterVerificationParkedValues(parkedValuesPerCertificate: Map<Long, BudgetCostsCalculationResultFull>)

    fun getCostCategoriesTotalEligible(reportIds: Set<Long>): BudgetCostsCalculationResultFull

}
