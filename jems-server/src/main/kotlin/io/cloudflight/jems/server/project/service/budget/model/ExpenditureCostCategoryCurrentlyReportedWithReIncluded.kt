package io.cloudflight.jems.server.project.service.budget.model

data class ExpenditureCostCategoryCurrentlyReportedWithReIncluded(
    val currentlyReported: BudgetCostsCalculationResultFull,
    val currentlyReportedReIncluded: BudgetCostsCalculationResultFull,
)
