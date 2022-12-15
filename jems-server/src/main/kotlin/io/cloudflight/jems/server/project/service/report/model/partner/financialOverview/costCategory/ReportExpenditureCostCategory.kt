package io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.costCategory

import io.cloudflight.jems.server.project.service.budget.model.BudgetCostsCalculationResultFull
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerBudgetOptions

data class ReportExpenditureCostCategory(
    val options: ProjectPartnerBudgetOptions,
    val totalsFromAF: BudgetCostsCalculationResultFull,
    val currentlyReported: BudgetCostsCalculationResultFull,
    val totalEligibleAfterControl: BudgetCostsCalculationResultFull,
    val previouslyReported: BudgetCostsCalculationResultFull,
)
