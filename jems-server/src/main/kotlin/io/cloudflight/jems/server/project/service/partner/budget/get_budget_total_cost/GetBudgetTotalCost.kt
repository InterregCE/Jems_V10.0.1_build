package io.cloudflight.jems.server.project.service.partner.budget.get_budget_total_cost

import io.cloudflight.jems.server.project.authorization.CanRetrieveProjectPartner
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
class GetBudgetTotalCost(
    private val getBudgetTotalCostCalculator: GetBudgetTotalCostCalculator
) : GetBudgetTotalCostInteractor {

    @Transactional(readOnly = true)
    @CanRetrieveProjectPartner
    override fun getBudgetTotalSpfCost(partnerId: Long, version: String?): BigDecimal {
        return getBudgetTotalCostCalculator.getBudgetTotalSpfCost(partnerId, version)
    }

    @Transactional(readOnly = true)
    @CanRetrieveProjectPartner
    override fun getBudgetTotalCost(partnerId: Long, version: String?): BigDecimal {
        return getBudgetTotalCostCalculator.getBudgetTotalCost(partnerId, version)
    }

}
