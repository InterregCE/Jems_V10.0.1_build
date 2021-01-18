package io.cloudflight.jems.server.project.service.budget.model

import java.math.BigDecimal

data class BudgetCostsCalculationResult(
    val staffCosts: BigDecimal,
    val travelCosts: BigDecimal,
    val officeAndAdministrationCosts: BigDecimal,
    val otherCosts: BigDecimal,
    val totalCosts: BigDecimal,
)
