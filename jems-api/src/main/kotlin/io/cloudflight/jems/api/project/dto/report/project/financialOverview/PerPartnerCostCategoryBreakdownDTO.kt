package io.cloudflight.jems.api.project.dto.report.project.financialOverview

data class PerPartnerCostCategoryBreakdownDTO(
    val partners: List<PerPartnerCostCategoryBreakdownLineDTO>,
    val totalCurrent: BudgetCostsCalculationResultFullDTO,
    val totalAfterControl: BudgetCostsCalculationResultFullDTO,
)
