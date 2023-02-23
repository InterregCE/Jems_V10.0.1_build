package io.cloudflight.jems.server.project.service.budget.model

data class BudgetCostsCurrentValuesWrapper(
    val currentlyReported: BudgetCostsCalculationResultFull,
    val currentlyReportedParked: BudgetCostsCalculationResultFull,
)
