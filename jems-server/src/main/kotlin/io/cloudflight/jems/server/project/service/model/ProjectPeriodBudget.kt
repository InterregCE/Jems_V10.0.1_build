package io.cloudflight.jems.server.project.service.model

import java.math.BigDecimal

data class ProjectPeriodBudget(
    val periodNumber: Int,
    val periodStart: Int,
    val periodEnd: Int,
    var totalBudgetPerPeriod: BigDecimal,
    val budgetPerPeriodDetail: BudgetCostsDetail,
    val lastPeriod: Boolean
)
