package io.cloudflight.jems.server.project.service.model.project_funds_per_period

import java.math.BigDecimal

data class ProjectPeriodFund(
    val periodNumber: Int,
    val totalFundsPerPeriod: BigDecimal
)
