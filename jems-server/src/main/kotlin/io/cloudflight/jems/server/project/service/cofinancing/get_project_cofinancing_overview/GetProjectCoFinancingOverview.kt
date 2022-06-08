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
    private val projectBudgetPersistence: ProjectBudgetPersistence,
    private val projectPartnerCoFinancingPersistence: ProjectPartnerCoFinancingPersistence,
    private val getBudgetTotalCost: GetBudgetTotalCost
) : GetProjectCoFinancingOverviewInteractor {

    @Transactional(readOnly = true)
    @CanRetrieveProjectForm
    override fun getProjectCoFinancingOverview(projectId: Long, version: String?): ProjectCoFinancingOverview {
        val partnerIds = projectBudgetPersistence.getPartnersForProjectId(projectId = projectId, version).mapTo(HashSet()) { it.id!! }
        val funds = projectPartnerCoFinancingPersistence.getAvailableFunds(partnerIds.first())

        val managementCoFinancingOverview = calculateCoFinancingOverview(
            partnerIds = partnerIds,
            getBudgetTotalCost = { getBudgetTotalCost.getBudgetTotalCost(it, version) },
            getCoFinancingAndContributions = { projectPartnerCoFinancingPersistence.getCoFinancingAndContributions(it, version) },
            funds = funds,
        )

        val spfCoFinancingOverview = calculateCoFinancingOverview(
            partnerIds = partnerIds,
            getBudgetTotalCost = { getBudgetTotalCost.getBudgetTotalSpfCost(it, version) },
            getCoFinancingAndContributions = { projectPartnerCoFinancingPersistence.getSpfCoFinancingAndContributions(it, version) },
            funds = funds,
        )

        return ProjectCoFinancingOverview(managementCoFinancingOverview, spfCoFinancingOverview)
    }
}
