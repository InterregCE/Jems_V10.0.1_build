package io.cloudflight.jems.server.project.service.partner.budget.update_budge_staff_costs

import io.cloudflight.jems.server.project.service.partner.model.BudgetStaffCostEntry

interface UpdateBudgetStaffCostsInteractor {
    fun updateBudgetStaffCosts(partnerId: Long, staffCosts: List<BudgetStaffCostEntry>): List<BudgetStaffCostEntry>
}
