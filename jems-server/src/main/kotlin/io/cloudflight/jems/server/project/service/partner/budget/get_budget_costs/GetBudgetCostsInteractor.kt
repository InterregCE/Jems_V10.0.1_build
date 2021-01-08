package io.cloudflight.jems.server.project.service.partner.budget.get_budget_costs

import io.cloudflight.jems.server.project.service.partner.model.BudgetCosts

interface GetBudgetCostsInteractor {
    fun getBudgetCosts(partnerId: Long): BudgetCosts
}
