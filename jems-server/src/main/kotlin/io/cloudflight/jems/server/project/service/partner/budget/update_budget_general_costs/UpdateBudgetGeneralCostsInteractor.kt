package io.cloudflight.jems.server.project.service.partner.budget.update_budget_general_costs

import io.cloudflight.jems.api.programme.dto.costoption.BudgetCategory
import io.cloudflight.jems.server.project.service.partner.model.BudgetGeneralCostEntry

interface UpdateBudgetGeneralCostsInteractor {
    fun updateBudgetGeneralCosts(
        partnerId: Long,
        budgetGeneralCosts: List<BudgetGeneralCostEntry>,
        budgetCategory: BudgetCategory
    ): List<BudgetGeneralCostEntry>
}
