package io.cloudflight.jems.server.project.service.partner.budget.updateBudgetUnitCosts

import io.cloudflight.jems.server.project.service.partner.model.BudgetUnitCostEntry

interface UpdateBudgetUnitCostsInteractor {
    fun updateBudgetUnitCosts(partnerId: Long, unitCosts: List<BudgetUnitCostEntry>): List<BudgetUnitCostEntry>
}
