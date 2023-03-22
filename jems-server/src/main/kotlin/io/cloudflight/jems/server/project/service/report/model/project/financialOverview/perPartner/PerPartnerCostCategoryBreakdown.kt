package io.cloudflight.jems.server.project.service.report.model.project.financialOverview.perPartner

import io.cloudflight.jems.server.project.service.budget.model.BudgetCostsCalculationResultFull

data class PerPartnerCostCategoryBreakdown(
    val partners: List<PerPartnerCostCategoryBreakdownLine>,
    val totalCurrent: BudgetCostsCalculationResultFull,
    val totalAfterControl: BudgetCostsCalculationResultFull,
)
