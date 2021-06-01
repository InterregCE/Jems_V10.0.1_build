package io.cloudflight.jems.server.project.service.partner.budget.update_budget_general_costs.update_budget_external_expertise_and_services

import io.cloudflight.jems.server.project.authorization.CanUpdateProjectPartner
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.partner.budget.BudgetCostValidator
import io.cloudflight.jems.server.project.service.partner.budget.ProjectPartnerBudgetCostsUpdatePersistence
import io.cloudflight.jems.server.project.service.partner.budget.ProjectPartnerBudgetOptionsPersistence
import io.cloudflight.jems.server.project.service.partner.budget.update_budget_general_costs.UpdateBudgetGeneralCosts
import io.cloudflight.jems.server.project.service.partner.model.BudgetGeneralCostEntry
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdateBudgetExternalExpertiseAndServicesCosts(
    private val persistence: ProjectPartnerBudgetCostsUpdatePersistence,
    private val projectPersistence: ProjectPersistence,
    budgetOptionsPersistence: ProjectPartnerBudgetOptionsPersistence,
    budgetCostValidator: BudgetCostValidator
) : UpdateBudgetExternalExpertiseAndServicesCostsInteractor,
    UpdateBudgetGeneralCosts(projectPersistence, budgetOptionsPersistence, budgetCostValidator) {

    @Transactional
    @CanUpdateProjectPartner
    override fun deleteAllBudgetGeneralCostsExceptFor(partnerId: Long, idsToKeep: Set<Long>) =
        persistence.deleteAllBudgetExternalExpertiseAndServicesCostsExceptFor(partnerId, idsToKeep)

    @Transactional
    @CanUpdateProjectPartner
    override fun createOrUpdateBudgetGeneralCosts(partnerId: Long, budgetGeneralCosts: List<BudgetGeneralCostEntry>) =
        persistence.createOrUpdateBudgetExternalExpertiseAndServicesCosts(
            projectPersistence.getProjectIdForPartner(partnerId),
            partnerId,
            budgetGeneralCosts
        )

}
