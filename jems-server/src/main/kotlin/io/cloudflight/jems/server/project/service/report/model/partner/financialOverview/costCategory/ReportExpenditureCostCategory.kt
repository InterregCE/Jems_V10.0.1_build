package io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.costCategory

import io.cloudflight.jems.server.project.service.budget.model.BudgetCostsCalculationResultFull
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerBudgetOptions
import org.springframework.validation.annotation.Validated

data class ReportExpenditureCostCategory(
    val options: ProjectPartnerBudgetOptions,
    val totalsFromAF: BudgetCostsCalculationResultFull,
    val currentlyReported: BudgetCostsCalculationResultFull,
    val totalEligibleAfterControl: BudgetCostsCalculationResultFull,
    val previouslyReported: BudgetCostsCalculationResultFull,
    val previouslyValidated: BudgetCostsCalculationResultFull,

    // Parking
    val currentlyReportedParked: BudgetCostsCalculationResultFull,
    val currentlyReportedReIncluded: BudgetCostsCalculationResultFull,
    val previouslyReportedParked: BudgetCostsCalculationResultFull,
)
