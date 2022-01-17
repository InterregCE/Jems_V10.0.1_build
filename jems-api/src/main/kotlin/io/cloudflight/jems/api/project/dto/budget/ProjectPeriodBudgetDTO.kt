package io.cloudflight.jems.api.project.dto.budget

import java.math.BigDecimal

data class ProjectPeriodBudgetDTO(
    val periodNumber: Int,
    val periodStart: Int,
    val periodEnd: Int,
    val totalBudgetPerPeriod: BigDecimal = BigDecimal.ZERO,
    val isLastPeriod: Boolean
)
