package io.cloudflight.jems.server.project.service.report.partner.financialOverview

import io.cloudflight.jems.server.project.service.budget.model.BudgetCostsCalculationResultFull
import io.cloudflight.jems.server.project.service.report.model.financialOverview.costCategory.ReportExpenditureCostCategory

interface ProjectReportExpenditureCostCategoryPersistence {

    fun getCostCategories(partnerId: Long, reportId: Long): ReportExpenditureCostCategory

    fun getCostCategoriesCumulative(reportIds: Set<Long>): BudgetCostsCalculationResultFull

    fun updateCurrentlyReportedValues(partnerId: Long, reportId: Long, currentlyReported: BudgetCostsCalculationResultFull)

}
