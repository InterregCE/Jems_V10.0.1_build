package io.cloudflight.jems.server.payments.model.advance

import java.math.BigDecimal
import java.time.LocalDate

data class AdvancePaymentSettlement(
    val id: Long,
    var number: Int,
    val amountSettled: BigDecimal,
    val settlementDate: LocalDate,
    val comment: String?,
)
