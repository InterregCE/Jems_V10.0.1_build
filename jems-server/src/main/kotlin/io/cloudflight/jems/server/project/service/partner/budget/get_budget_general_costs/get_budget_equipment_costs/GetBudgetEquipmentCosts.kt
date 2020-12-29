package io.cloudflight.jems.server.project.service.partner.budget.get_budget_general_costs.get_budget_equipment_costs

import io.cloudflight.jems.server.project.authorization.CanReadProjectPartner
import io.cloudflight.jems.server.project.service.partner.budget.ProjectPartnerBudgetPersistence
import io.cloudflight.jems.server.project.service.partner.budget.get_budget_general_costs.GetBudgetGeneralCosts
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetBudgetEquipmentCosts(private val persistence: ProjectPartnerBudgetPersistence) : GetBudgetEquipmentCostsInteractor, GetBudgetGeneralCosts() {

    @Transactional(readOnly = true)
    @CanReadProjectPartner
    override fun getBudgetGeneralEntries(partnerId: Long) =
        persistence.getBudgetEquipmentCosts(partnerId)

}
