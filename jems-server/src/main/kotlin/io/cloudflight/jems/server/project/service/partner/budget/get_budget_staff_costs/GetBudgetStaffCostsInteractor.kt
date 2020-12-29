package io.cloudflight.jems.server.project.service.partner.budget.get_budget_staff_costs

import io.cloudflight.jems.server.project.service.partner.model.BudgetStaffCostEntry

interface GetBudgetStaffCostsInteractor {
    fun getBudgetStaffCosts(partnerId: Long): List<BudgetStaffCostEntry>
}
