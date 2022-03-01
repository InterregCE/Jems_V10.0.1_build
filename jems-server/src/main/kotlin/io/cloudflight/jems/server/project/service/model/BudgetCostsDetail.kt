package io.cloudflight.jems.server.project.service.model

import java.math.BigDecimal

data class BudgetCostsDetail constructor(
    val unitCosts: BigDecimal = BigDecimal.ZERO,
    val lumpSumsCosts: BigDecimal = BigDecimal.ZERO,
    val externalCosts: BigDecimal = BigDecimal.ZERO,
    val equipmentCosts: BigDecimal = BigDecimal.ZERO,
    val infrastructureCosts: BigDecimal = BigDecimal.ZERO,
    val officeAndAdministrationCosts: BigDecimal = BigDecimal.ZERO,
    val travelCosts: BigDecimal = BigDecimal.ZERO,
    val staffCosts: BigDecimal = BigDecimal.ZERO,
    val otherCosts: BigDecimal = BigDecimal.ZERO
) {
    private constructor() : this(
        BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
        BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO
    )
}
