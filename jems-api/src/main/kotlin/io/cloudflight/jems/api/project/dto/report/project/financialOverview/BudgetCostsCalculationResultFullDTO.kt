package io.cloudflight.jems.api.project.dto.report.project.financialOverview

import java.math.BigDecimal

data class BudgetCostsCalculationResultFullDTO(
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
