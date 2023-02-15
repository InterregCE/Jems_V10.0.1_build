package io.cloudflight.jems.server.project.service.budget.model;

data class ExpenditureCostCategoryPreviouslyReportedWithParked(
    var previouslyReported: BudgetCostsCalculationResultFull,
    var previouslyReportedParked: BudgetCostsCalculationResultFull,
)
