package io.cloudflight.jems.server.project.service.budget.model

data class ExpenditureCostCategoryCurrentlyReportedWithParked(
    val currentlyReported: BudgetCostsCalculationResultFull,
    val currentlyReportedParked: BudgetCostsCalculationResultFull,
)
