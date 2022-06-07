package io.cloudflight.jems.server.project.service.partner.budget.update_budget_general_costs

import io.cloudflight.jems.api.programme.dto.costoption.BudgetCategory
import io.cloudflight.jems.server.common.exception.I18nValidationException
import io.cloudflight.jems.server.project.authorization.CanUpdateProjectPartner
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.partner.budget.BudgetCostValidator
import io.cloudflight.jems.server.project.service.partner.budget.ProjectPartnerBudgetOptionsPersistence
import io.cloudflight.jems.server.project.service.partner.budget.truncate
import io.cloudflight.jems.server.project.service.partner.model.BudgetGeneralCostEntry
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerBudgetOptions
import io.cloudflight.jems.server.project.service.partner.model.truncateBaseEntryNumbers
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
abstract class UpdateBudgetGeneralCosts(
    private val projectPersistence: ProjectPersistence,
    private val partnerPersistence: PartnerPersistence,
    private val budgetOptionsPersistence: ProjectPartnerBudgetOptionsPersistence,
    private val budgetCostValidator: BudgetCostValidator
) : UpdateBudgetGeneralCostsInteractor {

    @Transactional
    @CanUpdateProjectPartner
    override fun updateBudgetGeneralCosts(
        partnerId: Long,
        budgetGeneralCosts: List<BudgetGeneralCostEntry>,
        budgetCategory: BudgetCategory
    ): List<BudgetGeneralCostEntry> {
        val projectId = partnerPersistence.getProjectIdForPartnerId(partnerId)
        val callId = projectPersistence.getCallIdOfProject(projectId)
        val periods = budgetGeneralCosts.map { it.budgetPeriods }.flatten().toSet()
        budgetCostValidator.validateAgainstAFConfig(
            callId,
            periods,
            budgetCategory,
            budgetGeneralCosts.map { it.numberOfUnits },
            budgetGeneralCosts.map { Pair(it.unitCostId, it.unitType) }
        )

        budgetCostValidator.validateBaseEntries(budgetGeneralCosts)
        budgetCostValidator.validatePricePerUnits(budgetGeneralCosts.map { it.pricePerUnit })

        budgetCostValidator.validateBudgetPeriods(
            periods,
            projectPersistence.getProjectPeriods(projectId).map { it.number }.toSet()
        )

        throwIfOtherCostFlatRateIsSet(budgetOptionsPersistence.getBudgetOptions(partnerId))

        budgetCostValidator.validateAllowedRealCosts(
            callId,
            budgetGeneralCosts,
            budgetCategory
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
        }.toList())
    }

    protected abstract fun deleteAllBudgetGeneralCostsExceptFor(partnerId: Long, idsToKeep: Set<Long>)
    protected abstract fun createOrUpdateBudgetGeneralCosts(
        partnerId: Long,
        budgetGeneralCosts: List<BudgetGeneralCostEntry>
    ): List<BudgetGeneralCostEntry>

    private fun calculateRowSum(generalCostEntry: BudgetGeneralCostEntry) =
        generalCostEntry.pricePerUnit.multiply(generalCostEntry.numberOfUnits).truncate()

    private fun throwIfOtherCostFlatRateIsSet(budgetOptions: ProjectPartnerBudgetOptions?) {
        if (budgetOptions?.otherCostsOnStaffCostsFlatRate !== null)
            throw I18nValidationException(i18nKey = "project.partner.budget.not.allowed.because.of.otherCostsOnStaffCostsFlatRate")
    }
}
