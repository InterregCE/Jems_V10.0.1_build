package io.cloudflight.jems.server.project.service.partner.budget.update_budge_staff_costs

import io.cloudflight.jems.server.common.exception.I18nValidationException
import io.cloudflight.jems.server.project.authorization.CanUpdateProjectPartner
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.partner.budget.BudgetCostValidator
import io.cloudflight.jems.server.project.service.partner.budget.ProjectPartnerBudgetCostsUpdatePersistence
import io.cloudflight.jems.server.project.service.partner.budget.ProjectPartnerBudgetOptionsPersistence
import io.cloudflight.jems.server.project.service.partner.budget.truncate
import io.cloudflight.jems.server.project.service.partner.model.BudgetStaffCostEntry
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerBudgetOptions
import io.cloudflight.jems.server.project.service.partner.model.truncateBaseEntryNumbers
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdateBudgetStaffCosts(
    private val persistence: ProjectPartnerBudgetCostsUpdatePersistence,
    private val projectPersistence: ProjectPersistence,
    private val partnerPersistence: PartnerPersistence,
    private val budgetOptionsPersistence: ProjectPartnerBudgetOptionsPersistence,
    private val budgetCostValidator: BudgetCostValidator
) : UpdateBudgetStaffCostsInteractor {

    @Transactional
    @CanUpdateProjectPartner
    override fun updateBudgetStaffCosts(
        partnerId: Long,
        staffCosts: List<BudgetStaffCostEntry>
    ): List<BudgetStaffCostEntry> {

        budgetCostValidator.validateBaseEntries(staffCosts)
        budgetCostValidator.validatePricePerUnits(staffCosts.map { it.pricePerUnit })

        throwIfStaffCostFlatRateIsSet(budgetOptionsPersistence.getBudgetOptions(partnerId))

        val projectId = partnerPersistence.getProjectIdForPartnerId(partnerId)

        budgetCostValidator.validateBudgetPeriods(
            staffCosts.map { it.budgetPeriods }.flatten().toSet(),
            projectPersistence.getProjectPeriods(projectId).map { it.number }.toSet()
        )

        persistence.deleteAllBudgetStaffCostsExceptFor(
            partnerId = partnerId,
            idsToKeep = staffCosts.mapNotNullTo(HashSet()) { it.id }
        )

        return persistence.createOrUpdateBudgetStaffCosts(
            projectId,
            partnerId,
            staffCosts.map {
                it.apply {
                    it.rowSum = calculateRowSum(it)
                    this.truncateBaseEntryNumbers()
                    this.pricePerUnit.truncate()
                }
            }.toList()
        )
    }

}

private fun throwIfStaffCostFlatRateIsSet(budgetOptions: ProjectPartnerBudgetOptions?) {
    if (budgetOptions?.staffCostsFlatRate != null)
        throw I18nValidationException(i18nKey = "project.partner.budget.not.allowed.because.of.staffCostsFlatRate")
}

private fun calculateRowSum(staffCostEntry: BudgetStaffCostEntry) =
    staffCostEntry.pricePerUnit.multiply(staffCostEntry.numberOfUnits).truncate()
