package io.cloudflight.jems.server.project.service.partner.budget.update_budget_general_costs.update_budget_external_expertise_and_services

import io.cloudflight.jems.server.project.authorization.CanUpdateProjectPartner
import io.cloudflight.jems.server.project.service.partner.budget.BudgetCostEntriesValidator
import io.cloudflight.jems.server.project.service.partner.budget.ProjectPartnerBudgetPersistence
import io.cloudflight.jems.server.project.service.partner.budget.update_budget_general_costs.UpdateBudgetGeneralCosts
import io.cloudflight.jems.server.project.service.partner.model.BudgetGeneralCostEntry
import io.cloudflight.jems.server.project.service.partner.model.truncateNumbers
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdateBudgetExternalExpertiseAndServicesCosts(private val persistence: ProjectPartnerBudgetPersistence, private val budgetCostEntriesValidator: BudgetCostEntriesValidator) : UpdateBudgetExternalExpertiseAndServicesCostsInteractor, UpdateBudgetGeneralCosts(budgetCostEntriesValidator) {

    @Transactional
    @CanUpdateProjectPartner
    override fun deleteAllBudgetGeneralCostsExceptFor(partnerId: Long, idsToKeep: List<Long>) =
        persistence.deleteAllBudgetExternalExpertiseAndServicesCostsExceptFor(partnerId, idsToKeep)

    @Transactional
    @CanUpdateProjectPartner
    override fun createOrUpdateBudgetGeneralCosts(partnerId: Long, budgetGeneralCosts: List<BudgetGeneralCostEntry>) =
        persistence.createOrUpdateBudgetExternalExpertiseAndServicesCosts(partnerId, budgetGeneralCosts.map { it.apply { this.truncateNumbers() }})

}
