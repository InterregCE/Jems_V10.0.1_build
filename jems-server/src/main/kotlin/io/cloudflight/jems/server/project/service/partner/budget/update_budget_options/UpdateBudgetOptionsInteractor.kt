package io.cloudflight.jems.server.project.service.partner.budget.update_budget_options

interface UpdateBudgetOptionsInteractor {
    fun updateBudgetOptions(partnerId: Long, officeAdministrationFlatRate: Int?, staffCostsFlatRate: Int?)
}
