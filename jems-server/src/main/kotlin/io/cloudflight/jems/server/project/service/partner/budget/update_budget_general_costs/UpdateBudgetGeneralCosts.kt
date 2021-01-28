package io.cloudflight.jems.server.project.service.partner.budget.update_budget_general_costs

import io.cloudflight.jems.server.project.authorization.CanUpdateProjectPartner
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.partner.budget.BudgetCostValidator
import io.cloudflight.jems.server.project.service.partner.budget.truncate
import io.cloudflight.jems.server.project.service.partner.model.BudgetGeneralCostEntry
import io.cloudflight.jems.server.project.service.partner.model.truncateBaseEntryNumbers
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
abstract class UpdateBudgetGeneralCosts(
    private val projectPersistence: ProjectPersistence,
    private val budgetCostValidator: BudgetCostValidator
) : UpdateBudgetGeneralCostsInteractor {

    @Transactional
    @CanUpdateProjectPartner
    override fun updateBudgetGeneralCosts(
        partnerId: Long,
        budgetGeneralCosts: List<BudgetGeneralCostEntry>
    ): List<BudgetGeneralCostEntry> {

        budgetCostValidator.validateBaseEntries(budgetGeneralCosts)
        budgetCostValidator.validatePricePerUnits(budgetGeneralCosts.map { it.pricePerUnit })

        budgetCostValidator.validateBudgetPeriods(
            budgetGeneralCosts.map { it.budgetPeriods }.flatten().toSet(),
            projectPersistence.getProjectPeriods(
                projectPersistence.getProjectIdForPartner(partnerId)
            ).map { it.number }.toSet()
        )
        deleteAllBudgetGeneralCostsExceptFor(
            partnerId = partnerId,
            idsToKeep = budgetGeneralCosts.mapNotNullTo(HashSet()) { it.id }
        )

        return createOrUpdateBudgetGeneralCosts(partnerId, budgetGeneralCosts.map {
            it.apply {
                it.rowSum = calculateRowSum(it)
                this.truncateBaseEntryNumbers()
                this.pricePerUnit.truncate()
            }
        }.toSet())
    }

    protected abstract fun deleteAllBudgetGeneralCostsExceptFor(partnerId: Long, idsToKeep: Set<Long>)
    protected abstract fun createOrUpdateBudgetGeneralCosts(
        partnerId: Long,
        budgetGeneralCosts: Set<BudgetGeneralCostEntry>
    ): List<BudgetGeneralCostEntry>

    private fun calculateRowSum(generalCostEntry: BudgetGeneralCostEntry) =
        generalCostEntry.pricePerUnit.multiply(generalCostEntry.numberOfUnits).truncate()

}
