package io.cloudflight.jems.server.project.service.partner.budget.get_budget_costs

import io.cloudflight.jems.server.project.authorization.CanReadProjectPartner
import io.cloudflight.jems.server.project.service.partner.budget.ProjectPartnerBudgetCostsPersistence
import io.cloudflight.jems.server.project.service.partner.model.BudgetCosts
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetBudgetCosts(private val persistence: ProjectPartnerBudgetCostsPersistence) : GetBudgetCostsInteractor {

    @Transactional(readOnly = true)
    @CanReadProjectPartner
    override fun getBudgetCosts(partnerId: Long) =
        BudgetCosts(
            staffCosts = persistence.getBudgetStaffCosts(partnerId),
            travelCosts = persistence.getBudgetTravelAndAccommodationCosts(partnerId),
            externalCosts = persistence.getBudgetExternalExpertiseAndServicesCosts(partnerId),
            equipmentCosts = persistence.getBudgetEquipmentCosts(partnerId),
            infrastructureCosts = persistence.getBudgetInfrastructureAndWorksCosts(partnerId),
            unitCosts = persistence.getBudgetUnitCosts(partnerId)
        )
}
