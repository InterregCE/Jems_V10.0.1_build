package io.cloudflight.jems.server.project.service.partner.budget.update_budget_options

import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerBudgetOptions

interface UpdateBudgetOptionsInteractor {
    fun updateBudgetOptions(partnerId: Long, options: ProjectPartnerBudgetOptions)
}
