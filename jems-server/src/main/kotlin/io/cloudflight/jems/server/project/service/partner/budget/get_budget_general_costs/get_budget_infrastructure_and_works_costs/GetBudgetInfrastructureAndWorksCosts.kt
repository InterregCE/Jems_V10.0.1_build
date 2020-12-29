package io.cloudflight.jems.server.project.service.partner.budget.get_budget_general_costs.get_budget_infrastructure_and_works_costs

import io.cloudflight.jems.server.project.authorization.CanReadProjectPartner
import io.cloudflight.jems.server.project.service.partner.budget.ProjectPartnerBudgetPersistence
import io.cloudflight.jems.server.project.service.partner.budget.get_budget_general_costs.GetBudgetGeneralCosts
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetBudgetInfrastructureAndWorksCosts(private val persistence: ProjectPartnerBudgetPersistence) :GetBudgetInfrastructureAndWorksCostsInteractor, GetBudgetGeneralCosts() {

    @Transactional(readOnly = true)
    @CanReadProjectPartner
    override fun getBudgetGeneralEntries(partnerId: Long) =
        persistence.getBudgetInfrastructureAndWorksCosts(partnerId)

}
