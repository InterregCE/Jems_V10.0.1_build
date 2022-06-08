package io.cloudflight.jems.server.project.service.budget.get_budget_funds_per_period

import io.cloudflight.jems.server.project.service.model.project_funds_per_period.ProjectFundsPerPeriod

interface GetBudgetFundsPerPeriodInteractor {

    fun getBudgetFundsPerPeriod(projectId: Long, version: String? = null): ProjectFundsPerPeriod
}
