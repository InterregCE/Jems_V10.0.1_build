package io.cloudflight.jems.server.project.service.partner.budget.update_budget_general_costs.update_budget_infrastructure_and_works_costs

import io.cloudflight.jems.server.project.authorization.CanUpdateProjectPartner
import io.cloudflight.jems.server.project.service.partner.budget.ProjectPartnerBudgetPersistence
import io.cloudflight.jems.server.project.service.partner.budget.update_budget_general_costs.UpdateBudgetGeneralCosts
import io.cloudflight.jems.server.project.service.partner.model.BudgetGeneralCostEntry
import io.cloudflight.jems.server.project.service.partner.model.truncateNumbers
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdateBudgetInfrastructureAndWorksCosts(private val persistence: ProjectPartnerBudgetPersistence) : UpdateBudgetInfrastructureAndWorksCostsInteractor, UpdateBudgetGeneralCosts() {

    @Transactional
    @CanUpdateProjectPartner
    override fun deleteAllBudgetGeneralCostsExceptFor(partnerId: Long, idsToKeep: List<Long>) =
        persistence.deleteAllBudgetInfrastructureAndWorksCostsExceptFor(partnerId, idsToKeep)

    @Transactional
    @CanUpdateProjectPartner
    override fun createOrUpdateBudgetGeneralCosts(partnerId: Long, budgetGeneralCosts: List<BudgetGeneralCostEntry>) =
        persistence.createOrUpdateBudgetInfrastructureAndWorksCosts(partnerId, budgetGeneralCosts.map { it.apply { this.truncateNumbers() }})

}
