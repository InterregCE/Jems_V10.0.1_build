package io.cloudflight.jems.server.project.service.cofinancing.get_project_cofinancing_overview

import io.cloudflight.jems.server.project.authorization.CanRetrieveProjectForm
import io.cloudflight.jems.server.project.service.budget.ProjectBudgetPersistence
import io.cloudflight.jems.server.project.service.cofinancing.get_project_cofinancing_overview.CoFinancingOverviewCalculator.Companion.calculateCoFinancingOverview
import io.cloudflight.jems.server.project.service.cofinancing.model.ProjectCoFinancingOverview
import io.cloudflight.jems.server.project.service.partner.budget.get_budget_total_cost.GetBudgetTotalCost
import io.cloudflight.jems.server.project.service.partner.cofinancing.ProjectPartnerCoFinancingPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetProjectCoFinancingOverview(
    private val getProjectCoFinancingOverviewCalculatorService: GetProjectCoFinancingOverviewCalculatorService
) : GetProjectCoFinancingOverviewInteractor {

    @Transactional(readOnly = true)
    @CanRetrieveProjectForm
    override fun getProjectCoFinancingOverview(projectId: Long, version: String?): ProjectCoFinancingOverview {
        return getProjectCoFinancingOverviewCalculatorService.getProjectCoFinancingOverview(projectId, version)
    }
}
