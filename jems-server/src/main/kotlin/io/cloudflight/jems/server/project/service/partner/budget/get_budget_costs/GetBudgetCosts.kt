package io.cloudflight.jems.server.project.service.partner.budget.get_budget_costs

import io.cloudflight.jems.server.project.authorization.CanRetrieveProjectPartner
import io.cloudflight.jems.server.project.service.partner.budget.ProjectPartnerBudgetCostsPersistence
import io.cloudflight.jems.server.project.service.partner.model.BudgetCosts
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetBudgetCosts(private val persistence: ProjectPartnerBudgetCostsPersistence) : GetBudgetCostsInteractor {

    @Transactional(readOnly = true)
    @CanRetrieveProjectPartner
    override fun getBudgetCosts(partnerId: Long, version: String?) =
        BudgetCosts(
            staffCosts = persistence.getBudgetStaffCosts(setOf(partnerId), version),
            travelCosts = persistence.getBudgetTravelAndAccommodationCosts(setOf(partnerId), version),
            externalCosts = persistence.getBudgetExternalExpertiseAndServicesCosts(setOf(partnerId), version),
            equipmentCosts = persistence.getBudgetEquipmentCosts(setOf(partnerId), version),
            infrastructureCosts = persistence.getBudgetInfrastructureAndWorksCosts(setOf(partnerId), version),
            unitCosts = persistence.getBudgetUnitCosts(setOf(partnerId), version),
            spfCosts = persistence.getBudgetSpfCosts(setOf(partnerId), version)
        )
}
