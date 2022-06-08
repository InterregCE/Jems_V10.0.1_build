package io.cloudflight.jems.server.project.service.partner.budget.get_budget_total_cost

import java.math.BigDecimal

interface GetBudgetTotalCostInteractor {
    fun getBudgetTotalCost(partnerId: Long, version: String? = null): BigDecimal

    fun getBudgetTotalSpfCost(partnerId: Long, version: String? = null): BigDecimal
}
