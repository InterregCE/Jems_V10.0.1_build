package io.cloudflight.jems.server.project.service.cofinancing.get_project_cofinancing_overview

import io.cloudflight.jems.server.project.service.budget.ProjectBudgetPersistence
import io.cloudflight.jems.server.project.service.cofinancing.model.ProjectCoFinancingOverview
import io.cloudflight.jems.server.project.service.partner.budget.get_budget_total_cost.GetBudgetTotalCostCalculator
import io.cloudflight.jems.server.project.service.partner.cofinancing.ProjectPartnerCoFinancingPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetProjectCoFinancingOverviewCalculatorService(
    private val projectBudgetPersistence: ProjectBudgetPersistence,
    private val projectPartnerCoFinancingPersistence: ProjectPartnerCoFinancingPersistence,
    private val getBudgetTotalCostCalculator: GetBudgetTotalCostCalculator
) {

    @Transactional(readOnly = true)
    fun getProjectCoFinancingOverview(projectId: Long, version: String?): ProjectCoFinancingOverview {
        val partnerIds = projectBudgetPersistence.getPartnersForProjectId(projectId = projectId, version).mapTo(HashSet()) { it.id!! }
        val funds = projectPartnerCoFinancingPersistence.getAvailableFunds(partnerIds.first())

        val managementCoFinancingOverview = CoFinancingOverviewCalculator.calculateCoFinancingOverview(
            partnerIds = partnerIds,
            getBudgetTotalCost = { getBudgetTotalCostCalculator.getBudgetTotalManagementCost(it, version) },
            getCoFinancingAndContributions = { projectPartnerCoFinancingPersistence.getCoFinancingAndContributions(it, version) },
            funds = funds,
        )

        val spfCoFinancingOverview = CoFinancingOverviewCalculator.calculateCoFinancingOverview(
            partnerIds = partnerIds,
            getBudgetTotalCost = { getBudgetTotalCostCalculator.getBudgetTotalSpfCost(it, version) },
            getCoFinancingAndContributions = { projectPartnerCoFinancingPersistence.getSpfCoFinancingAndContributions(it, version) },
            funds = funds,
        )
        return ProjectCoFinancingOverview(managementCoFinancingOverview, spfCoFinancingOverview)
    }
}
