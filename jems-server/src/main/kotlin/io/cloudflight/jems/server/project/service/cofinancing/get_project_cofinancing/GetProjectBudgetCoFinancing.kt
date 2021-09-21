package io.cloudflight.jems.server.project.service.cofinancing.get_project_cofinancing

import io.cloudflight.jems.server.project.authorization.CanRetrieveProjectForm
import io.cloudflight.jems.server.project.repository.partner.cofinancing.ProjectPartnerCoFinancingPersistenceProvider
import io.cloudflight.jems.server.project.service.budget.ProjectBudgetPersistence
import io.cloudflight.jems.server.project.service.cofinancing.model.PartnerBudgetCoFinancing
import io.cloudflight.jems.server.project.service.partner.budget.get_budget_total_cost.GetBudgetTotalCost
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerCoFinancingAndContribution
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetProjectBudgetCoFinancing(
    private val projectBudgetPersistence: ProjectBudgetPersistence,
    private val projectPartnerCoFinancingPersistenceProvider: ProjectPartnerCoFinancingPersistenceProvider,
    private val getBudgetTotalCost: GetBudgetTotalCost
) : GetProjectBudgetCoFinancingInteractor {

    @Transactional(readOnly = true)
    @CanRetrieveProjectForm
    override fun getBudgetCoFinancing(projectId: Long, version: String?): List<PartnerBudgetCoFinancing> {
        val partners = projectBudgetPersistence.getPartnersForProjectId(projectId = projectId, version).associateBy { it.id!! }

        val budgetCoFinancingContributions: MutableMap<Long, ProjectPartnerCoFinancingAndContribution> = mutableMapOf()

        partners.keys.forEach {
            budgetCoFinancingContributions.put(it,
                projectPartnerCoFinancingPersistenceProvider.getCoFinancingAndContributions(it, version)
            )
        }

        return partners.map { (partnerId, partner) ->
            PartnerBudgetCoFinancing(
                partner = partner,
                budgetCoFinancingContributions[partnerId],
                total = getBudgetTotalCost.getBudgetTotalCost(partnerId, version)
            )
        }

    }

}
