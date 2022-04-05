package io.cloudflight.jems.api.currency

import java.math.BigDecimal

data class CurrencyDTO (
    val code: String,
    val year: Int,
    val month: Int,
    val name: String,
    val conversionRate: BigDecimal
)
