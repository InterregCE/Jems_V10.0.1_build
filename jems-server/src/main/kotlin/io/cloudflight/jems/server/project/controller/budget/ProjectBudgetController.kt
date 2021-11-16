package io.cloudflight.jems.server.project.controller.budget

import io.cloudflight.jems.api.project.budget.ProjectBudgetApi
import io.cloudflight.jems.server.project.service.budget.get_partner_budget_per_period.GetPartnerBudgetPerPeriodInteractor
import org.springframework.web.bind.annotation.RestController

@RestController
class ProjectBudgetController(
    private val getPartnerBudgetPerPeriodInteractor: GetPartnerBudgetPerPeriodInteractor,
) : ProjectBudgetApi {

    override fun getProjectPartnerBudgetPerPeriod(projectId: Long, version: String?) =
        this.getPartnerBudgetPerPeriodInteractor.getPartnerBudgetPerPeriod(projectId, version).map { it.toDto() }

}
