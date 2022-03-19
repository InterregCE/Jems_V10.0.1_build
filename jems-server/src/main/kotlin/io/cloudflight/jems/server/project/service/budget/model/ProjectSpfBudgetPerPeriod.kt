package io.cloudflight.jems.server.project.service.budget.model

import java.math.BigDecimal

data class ProjectSpfBudgetPerPeriod(
    val periodNumber: Int = 0,
    val spfCostPerPeriod: BigDecimal = BigDecimal.ZERO,
)
