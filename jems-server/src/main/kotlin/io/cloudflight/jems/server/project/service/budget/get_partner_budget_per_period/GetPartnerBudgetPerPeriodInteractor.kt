package io.cloudflight.jems.server.project.service.budget.get_partner_budget_per_period

import io.cloudflight.jems.server.project.service.model.ProjectBudgetOverviewPerPartnerPerPeriod

interface GetPartnerBudgetPerPeriodInteractor {
    fun getPartnerBudgetPerPeriod(projectId: Long, version: String? = null): ProjectBudgetOverviewPerPartnerPerPeriod
}
