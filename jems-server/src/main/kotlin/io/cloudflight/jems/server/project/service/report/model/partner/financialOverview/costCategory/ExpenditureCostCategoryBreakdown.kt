package io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.costCategory

data class ExpenditureCostCategoryBreakdown(
    val staff: ExpenditureCostCategoryBreakdownLine,
    val office: ExpenditureCostCategoryBreakdownLine,
    val travel: ExpenditureCostCategoryBreakdownLine,
    val external: ExpenditureCostCategoryBreakdownLine,
    val equipment: ExpenditureCostCategoryBreakdownLine,
    val infrastructure: ExpenditureCostCategoryBreakdownLine,
    val other: ExpenditureCostCategoryBreakdownLine,
    val lumpSum: ExpenditureCostCategoryBreakdownLine,
    val unitCost: ExpenditureCostCategoryBreakdownLine,
    val spfCost: ExpenditureCostCategoryBreakdownLine,
    val total: ExpenditureCostCategoryBreakdownLine,
) {
    fun totalBudgetWithoutSpf() = total.totalEligibleBudget.minus(spfCost.totalEligibleBudget)
}
