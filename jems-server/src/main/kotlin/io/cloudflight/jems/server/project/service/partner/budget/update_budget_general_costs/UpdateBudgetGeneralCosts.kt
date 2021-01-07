package io.cloudflight.jems.server.project.service.partner.budget.update_budget_general_costs

import io.cloudflight.jems.server.project.authorization.CanUpdateProjectPartner
import io.cloudflight.jems.server.project.service.partner.budget.BudgetCostEntriesValidator
import io.cloudflight.jems.server.project.service.partner.model.BudgetGeneralCostEntry
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
abstract class UpdateBudgetGeneralCosts(private val budgetCostEntriesValidator: BudgetCostEntriesValidator) : UpdateBudgetGeneralCostsInteractor {

    @Transactional
    @CanUpdateProjectPartner
    override fun updateBudgetGeneralCosts(partnerId: Long, budgetGeneralCosts: List<BudgetGeneralCostEntry>): List<BudgetGeneralCostEntry> {
        budgetCostEntriesValidator.validate(budgetGeneralCosts)
        deleteAllBudgetGeneralCostsExceptFor(
            partnerId = partnerId,
            idsToKeep = budgetGeneralCosts.filter { it.id !== null }.map { it.id!! }
        )

        return createOrUpdateBudgetGeneralCosts(partnerId, budgetGeneralCosts)
    }

    protected abstract fun deleteAllBudgetGeneralCostsExceptFor(partnerId: Long, idsToKeep: List<Long>)
    protected abstract fun createOrUpdateBudgetGeneralCosts(partnerId: Long, budgetGeneralCosts: List<BudgetGeneralCostEntry>): List<BudgetGeneralCostEntry>

}
