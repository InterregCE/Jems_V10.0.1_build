package io.cloudflight.jems.server.project.service.partner.budget.get_budget_general_costs

import io.cloudflight.jems.server.project.authorization.CanReadProjectPartner
import io.cloudflight.jems.server.project.service.partner.model.BudgetGeneralCostEntry
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
abstract class GetBudgetGeneralCosts : GetBudgetGeneralCostsInteractor {

    @Transactional(readOnly = true)
    @CanReadProjectPartner
    override fun getBudgetGeneralCosts(partnerId: Long) =
        getBudgetGeneralEntries(partnerId)

    protected abstract fun getBudgetGeneralEntries(partnerId: Long): List<BudgetGeneralCostEntry>
}
