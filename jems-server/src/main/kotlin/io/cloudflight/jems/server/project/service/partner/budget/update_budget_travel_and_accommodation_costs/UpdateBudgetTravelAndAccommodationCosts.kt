package io.cloudflight.jems.server.project.service.partner.budget.update_budget_travel_and_accommodation_costs

import io.cloudflight.jems.api.programme.dto.costoption.BudgetCategory
import io.cloudflight.jems.server.common.exception.I18nValidationException
import io.cloudflight.jems.server.project.authorization.CanUpdateProjectPartner
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.customCostOptions.ProjectUnitCostPersistence
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.partner.budget.BudgetCostValidator
import io.cloudflight.jems.server.project.service.partner.budget.ProjectPartnerBudgetCostsUpdatePersistence
import io.cloudflight.jems.server.project.service.partner.budget.ProjectPartnerBudgetOptionsPersistence
import io.cloudflight.jems.server.project.service.partner.budget.truncate
import io.cloudflight.jems.server.project.service.partner.model.BudgetTravelAndAccommodationCostEntry
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerBudgetOptions
import io.cloudflight.jems.server.project.service.partner.model.truncateBaseEntryNumbers
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdateBudgetTravelAndAccommodationCosts(
    private val persistence: ProjectPartnerBudgetCostsUpdatePersistence,
    private val optionsPersistence: ProjectPartnerBudgetOptionsPersistence,
    private val projectPersistence: ProjectPersistence,
    private val partnerPersistence: PartnerPersistence,
    private val budgetCostValidator: BudgetCostValidator,
    private val projectUnitCostPersistence: ProjectUnitCostPersistence,
) : UpdateBudgetTravelAndAccommodationCostsInteractor {

    @Transactional
    @CanUpdateProjectPartner
    override fun updateBudgetTravelAndAccommodationCosts(
        partnerId: Long,
        travelCosts: List<BudgetTravelAndAccommodationCostEntry>
    ): List<BudgetTravelAndAccommodationCostEntry> {
        val projectId = partnerPersistence.getProjectIdForPartnerId(partnerId)
        val callId = projectPersistence.getCallIdOfProject(projectId)
        val periods = travelCosts.map { it.budgetPeriods }.flatten().toSet()
        budgetCostValidator.validateAgainstAFConfig(
            callId,
            periods,
            BudgetCategory.TravelAndAccommodationCosts,
            travelCosts.map { it.numberOfUnits },
            travelCosts.map { Pair(it.unitCostId, it.unitType) }
        )

        budgetCostValidator.validateBaseEntries(travelCosts)
        budgetCostValidator.validatePricePerUnits(travelCosts.map { it.pricePerUnit })
        if (travelCosts.any { it.unitCostId != null }) {
            budgetCostValidator.validateAllowedUnitCosts(
                availableUnitCosts = projectUnitCostPersistence.getAvailableUnitCostsForProjectId(projectId),
                travelCosts.filter { it.unitCostId != null }
                    .map { BudgetCostValidator.UnitCostEntry(it.unitCostId!!, it.pricePerUnit, it.unitType) }
            )
        }

        throwIfTravelOrOtherCostFlatRateAreSet(optionsPersistence.getBudgetOptions(partnerId))

        budgetCostValidator.validateAllowedRealCosts(
            callId,
            travelCosts,
            BudgetCategory.TravelAndAccommodationCosts
        )

        budgetCostValidator.validateBudgetPeriods(
            travelCosts.map { it.budgetPeriods }.flatten().toSet(),
            projectPersistence.getProjectPeriods(projectId).map { it.number }.toSet()
        )

        persistence.deleteAllBudgetTravelAndAccommodationCostsExceptFor(
            partnerId = partnerId,
            idsToKeep = travelCosts.mapNotNullTo(HashSet()) { it.id }
        )

        return persistence.createOrUpdateBudgetTravelAndAccommodationCosts(
            projectId,
            partnerId,
            travelCosts.map {
                it.apply {
                    it.rowSum = calculateRowSum(it)
                    this.truncateBaseEntryNumbers()
                    this.pricePerUnit.truncate()
                }
            }.toList()
        )
    }

    private fun throwIfTravelOrOtherCostFlatRateAreSet(budgetOptions: ProjectPartnerBudgetOptions?) {
        if (budgetOptions?.travelAndAccommodationOnStaffCostsFlatRate !== null)
            throw I18nValidationException(i18nKey = "project.partner.budget.not.allowed.because.of.travelAndAccommodationOnStaffCostsFlatRate")
        if (budgetOptions?.otherCostsOnStaffCostsFlatRate !== null)
            throw I18nValidationException(i18nKey = "project.partner.budget.not.allowed.because.of.otherCostsOnStaffCostsFlatRate")
    }

    private fun calculateRowSum(travelCostEntry: BudgetTravelAndAccommodationCostEntry) =
        travelCostEntry.pricePerUnit.multiply(travelCostEntry.numberOfUnits).truncate()

}
