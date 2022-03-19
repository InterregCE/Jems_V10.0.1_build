package io.cloudflight.jems.server.project.controller.budget

import io.cloudflight.jems.api.project.budget.ProjectFundsApi
import io.cloudflight.jems.api.project.dto.budget.ProjectFundsPerPeriodDTO
import io.cloudflight.jems.server.project.service.budget.get_budget_funds_per_period.GetBudgetFundsPerPeriodInteractor
import org.springframework.web.bind.annotation.RestController

@RestController
class ProjectFundsController(
    private val getBudgetFundsPerPeriodInteractor: GetBudgetFundsPerPeriodInteractor,
) : ProjectFundsApi {

    override fun getProjectBudgetFundsPerPeriod(projectId: Long, version: String?): ProjectFundsPerPeriodDTO =
        this.getBudgetFundsPerPeriodInteractor.getBudgetFundsPerPeriod(projectId, version).toDto()
}

