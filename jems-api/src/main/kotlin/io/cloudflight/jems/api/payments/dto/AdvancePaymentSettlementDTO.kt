package io.cloudflight.jems.api.payments.dto

import java.math.BigDecimal
import java.time.LocalDate

data class AdvancePaymentSettlementDTO(
    val id: Long,
    val number: Int,
    val amountSettled: BigDecimal,
    val settlementDate: LocalDate,
    val comment: String?,
)
