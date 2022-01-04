package io.cloudflight.jems.server.project.controller.budget

import io.cloudflight.jems.api.project.budget.ProjectBudgetApi
import io.cloudflight.jems.api.project.dto.budget.ProjectUnitCostDTO
import io.cloudflight.jems.server.project.controller.unitcost.toDto
import io.cloudflight.jems.server.project.service.budget.get_partner_budget_per_funds.GetPartnerBudgetPerFundInteractor
import io.cloudflight.jems.server.project.service.budget.get_partner_budget_per_period.GetPartnerBudgetPerPeriodInteractor
import io.cloudflight.jems.server.project.service.unitcost.get_project_unit_costs.GetProjectUnitCostsInteractor
import org.springframework.web.bind.annotation.RestController

@RestController
class ProjectBudgetController(
    private val getPartnerBudgetPerPeriodInteractor: GetPartnerBudgetPerPeriodInteractor,
    private val getProjectUnitCostsInteractor: GetProjectUnitCostsInteractor,
    private val getPartnerBudgetPerFundInteractor: GetPartnerBudgetPerFundInteractor
) : ProjectBudgetApi {

    override fun getProjectPartnerBudgetPerPeriod(projectId: Long, version: String?) =
        this.getPartnerBudgetPerPeriodInteractor.getPartnerBudgetPerPeriod(projectId, version).toDto()

    override fun getProjectUnitCosts(projectId: Long, version: String?): List<ProjectUnitCostDTO> =
        getProjectUnitCostsInteractor.getProjectUnitCost(projectId, version).toDto()

    override fun getProjectPartnerBudgetPerFund(projectId: Long, version: String?) =
        this.getPartnerBudgetPerFundInteractor.getProjectPartnerBudgetPerFund(projectId, version).map { it.toDto() }
}
