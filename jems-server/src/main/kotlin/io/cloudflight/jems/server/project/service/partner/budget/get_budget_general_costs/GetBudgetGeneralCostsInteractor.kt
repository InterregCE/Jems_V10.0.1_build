package io.cloudflight.jems.server.project.service.partner.budget.get_budget_general_costs

import io.cloudflight.jems.server.project.service.partner.model.BudgetGeneralCostEntry

interface GetBudgetGeneralCostsInteractor {
    fun getBudgetGeneralCosts(partnerId: Long): List<BudgetGeneralCostEntry>
}
