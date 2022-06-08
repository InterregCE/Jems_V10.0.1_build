package io.cloudflight.jems.server.project.service.partner.budget.updateBudgetUnitCosts

import io.cloudflight.jems.server.call.service.CallPersistence
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.exception.I18nValidationException
import io.cloudflight.jems.server.project.authorization.CanUpdateProjectPartner
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.partner.budget.BudgetCostValidator
import io.cloudflight.jems.server.project.service.partner.budget.ProjectPartnerBudgetCostsUpdatePersistence
import io.cloudflight.jems.server.project.service.partner.budget.ProjectPartnerBudgetOptionsPersistence
import io.cloudflight.jems.server.project.service.partner.budget.truncate
import io.cloudflight.jems.server.project.service.partner.model.BudgetUnitCostEntry
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerBudgetOptions
import io.cloudflight.jems.server.project.service.partner.model.truncateBaseEntryNumbers
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
class UpdateBudgetUnitCosts(
    private val persistence: ProjectPartnerBudgetCostsUpdatePersistence,
    private val projectPersistence: ProjectPersistence,
    private val partnerPersistence: PartnerPersistence,
    private val callPersistence: CallPersistence,
    private val budgetOptionsPersistence: ProjectPartnerBudgetOptionsPersistence,
    private val budgetCostValidator: BudgetCostValidator
) : UpdateBudgetUnitCostsInteractor {

    @Transactional
    @CanUpdateProjectPartner
    @ExceptionWrapper(UpdateBudgetUnitCostsException::class)
    override fun updateBudgetUnitCosts(
        partnerId: Long,
        unitCosts: List<BudgetUnitCostEntry>
    ): List<BudgetUnitCostEntry> {
        budgetCostValidator.validateBaseEntries(unitCosts)

        throwIfOtherCostFlatRateIsSet(budgetOptionsPersistence.getBudgetOptions(partnerId))

        val projectId = partnerPersistence.getProjectIdForPartnerId(partnerId)
        if (unitCosts.isNotEmpty())
            validateSectionIsAllowedToBeSet(projectId = projectId)

        val unitCostPerUnitById =
            projectPersistence.getProjectUnitCosts(projectId).associateBy({ it.id }, { it.costPerUnit })

        persistence.deleteAllUnitCostsExceptFor(
            partnerId = partnerId,
            idsToKeep = unitCosts.mapNotNullTo(HashSet()) { it.id }
        )

        return persistence.createOrUpdateBudgetUnitCosts(projectId, partnerId,
            unitCosts.map {
                it.apply {
                    it.rowSum = calculateRowSum(it.numberOfUnits, unitCostPerUnitById[it.unitCostId])
                    it.truncateBaseEntryNumbers()
                }
            }.toList()
        )
    }

    private fun validateSectionIsAllowedToBeSet(projectId: Long) {
        val multipleCategoryUnitCosts = callPersistence.getCallByProjectId(projectId).unitCosts.filter { it.isMultipleCategoryUnitCost() }
        if (multipleCategoryUnitCosts.isEmpty()) {
            throw UnitCostsBudgetSectionIsNotAllowed()
        }
    }

    private fun throwIfOtherCostFlatRateIsSet(budgetOptions: ProjectPartnerBudgetOptions?) {
        if (budgetOptions?.otherCostsOnStaffCostsFlatRate !== null)
            throw I18nValidationException(i18nKey = "project.partner.budget.not.allowed.because.of.otherCostsOnStaffCostsFlatRate")
    }

    private fun calculateRowSum(numberOfUnits: BigDecimal, projectUnitCosts: BigDecimal?) =
        numberOfUnits.multiply(projectUnitCosts).truncate()


}
