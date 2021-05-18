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
            staffCosts = persistence.getBudgetStaffCosts(partnerId, version),
            travelCosts = persistence.getBudgetTravelAndAccommodationCosts(partnerId, version),
            externalCosts = persistence.getBudgetExternalExpertiseAndServicesCosts(partnerId, version),
            equipmentCosts = persistence.getBudgetEquipmentCosts(partnerId, version),
            infrastructureCosts = persistence.getBudgetInfrastructureAndWorksCosts(partnerId, version),
            unitCosts = persistence.getBudgetUnitCosts(partnerId, version)
        )
}
