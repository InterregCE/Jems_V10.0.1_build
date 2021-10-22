package io.cloudflight.jems.api.project.dto.budget

import java.math.BigDecimal

data class ProjectPeriodFundDTO(
    val periodNumber: Int,
    val totalFundsPerPeriod: BigDecimal = BigDecimal.ZERO
)
