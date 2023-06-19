package io.cloudflight.jems.server.project.service.budget.model;

data class ExpenditureCostCategoryPreviouslyReportedWithParked(
    val previouslyReported: BudgetCostsCalculationResultFull,
    val previouslyReportedParked: BudgetCostsCalculationResultFull,
    val previouslyValidated: BudgetCostsCalculationResultFull,
)
