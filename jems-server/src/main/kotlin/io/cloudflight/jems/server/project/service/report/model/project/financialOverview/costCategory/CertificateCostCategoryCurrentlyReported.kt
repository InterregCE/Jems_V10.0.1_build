package io.cloudflight.jems.server.project.service.report.model.project.financialOverview.costCategory

import io.cloudflight.jems.server.project.service.budget.model.BudgetCostsCalculationResultFull

data class CertificateCostCategoryCurrentlyReported(
    val currentlyReported: BudgetCostsCalculationResultFull,
)
