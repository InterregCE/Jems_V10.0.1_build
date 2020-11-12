package io.cloudflight.jems.server.project.service.partner.budget.get_budget_options

import io.cloudflight.jems.server.project.service.budget.model.ProjectPartnerBudgetOptions

interface GetBudgetOptionsInteractor {
    fun getBudgetOptions(partnerId: Long): ProjectPartnerBudgetOptions?
}
