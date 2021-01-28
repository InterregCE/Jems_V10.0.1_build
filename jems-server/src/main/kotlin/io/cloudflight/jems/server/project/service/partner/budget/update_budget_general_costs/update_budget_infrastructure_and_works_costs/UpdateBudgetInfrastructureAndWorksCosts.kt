package io.cloudflight.jems.server.project.service.partner.budget.update_budget_general_costs.update_budget_infrastructure_and_works_costs

import io.cloudflight.jems.server.project.authorization.CanUpdateProjectPartner
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.partner.budget.BudgetCostValidator
import io.cloudflight.jems.server.project.service.partner.budget.ProjectPartnerBudgetCostsUpdatePersistence
import io.cloudflight.jems.server.project.service.partner.budget.update_budget_general_costs.UpdateBudgetGeneralCosts
import io.cloudflight.jems.server.project.service.partner.model.BudgetGeneralCostEntry
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdateBudgetInfrastructureAndWorksCosts(
    private val persistence: ProjectPartnerBudgetCostsUpdatePersistence,
    private val projectPersistence: ProjectPersistence,
    budgetCostValidator: BudgetCostValidator
) : UpdateBudgetInfrastructureAndWorksCostsInteractor,
    UpdateBudgetGeneralCosts(projectPersistence, budgetCostValidator) {

    @Transactional
    @CanUpdateProjectPartner
    override fun deleteAllBudgetGeneralCostsExceptFor(partnerId: Long, idsToKeep: Set<Long>) =
        persistence.deleteAllBudgetInfrastructureAndWorksCostsExceptFor(partnerId, idsToKeep)

    @Transactional
    @CanUpdateProjectPartner
    override fun createOrUpdateBudgetGeneralCosts(partnerId: Long, budgetGeneralCosts: Set<BudgetGeneralCostEntry>) =
        persistence.createOrUpdateBudgetInfrastructureAndWorksCosts(
            projectPersistence.getProjectIdForPartner(partnerId),
            partnerId,
            budgetGeneralCosts)

}
