package io.cloudflight.jems.server.project.service.budget.model

import java.math.BigDecimal

data class BudgetCostsCalculationResultFull(
    val staff: BigDecimal,
    val office: BigDecimal,
    val travel: BigDecimal,
    val external: BigDecimal,
    val equipment: BigDecimal,
    val infrastructure: BigDecimal,
    val other: BigDecimal,
    val lumpSum: BigDecimal,
    val unitCost: BigDecimal,
    val sum: BigDecimal,
)
