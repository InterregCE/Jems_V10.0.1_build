package io.cloudflight.jems.server.project.service.partner.budget.update_budget_unit_costs

import io.cloudflight.jems.server.project.service.partner.model.BudgetUnitCostEntry

interface UpdateBudgetUnitCostsInteractor {
    fun updateBudgetUnitCosts(partnerId: Long, unitCosts: List<BudgetUnitCostEntry>): List<BudgetUnitCostEntry>
}
