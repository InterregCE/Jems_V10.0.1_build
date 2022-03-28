package io.cloudflight.jems.server.currency.service.model

import java.math.BigDecimal

data class CurrencyConversion (
    val code: String,
    val year: Int,
    val month: Int,
    val name: String,
    val conversionRate: BigDecimal
)
