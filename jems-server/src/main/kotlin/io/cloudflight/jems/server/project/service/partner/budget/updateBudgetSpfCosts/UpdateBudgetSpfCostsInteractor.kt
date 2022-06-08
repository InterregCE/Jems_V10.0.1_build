package io.cloudflight.jems.server.project.service.partner.budget.updateBudgetSpfCosts

import io.cloudflight.jems.server.project.service.partner.model.BudgetSpfCostEntry

interface UpdateBudgetSpfCostsInteractor {
    fun updateBudgetSpfCosts(partnerId: Long, spfCosts: List<BudgetSpfCostEntry>): List<BudgetSpfCostEntry>
}
