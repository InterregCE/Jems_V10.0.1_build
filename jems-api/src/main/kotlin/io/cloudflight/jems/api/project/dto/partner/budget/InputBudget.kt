package io.cloudflight.jems.api.project.dto.partner.budget

import java.math.BigDecimal

data class InputBudget(
    val id: Long? = null,
    val numberOfUnits: BigDecimal,
    val pricePerUnit: BigDecimal
)
