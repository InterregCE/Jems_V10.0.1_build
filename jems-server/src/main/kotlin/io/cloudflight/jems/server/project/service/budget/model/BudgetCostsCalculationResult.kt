package io.cloudflight.jems.server.project.service.budget.model

import java.math.BigDecimal

data class BudgetCostsCalculationResult(
    val staffCosts: BigDecimal = BigDecimal.ZERO,
    val travelCosts: BigDecimal = BigDecimal.ZERO,
    val officeAndAdministrationCosts: BigDecimal = BigDecimal.ZERO,
    val otherCosts: BigDecimal = BigDecimal.ZERO,
    val totalCosts: BigDecimal = BigDecimal.ZERO,
)
