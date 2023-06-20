package io.cloudflight.jems.api.payments.dto

import java.math.BigDecimal
import java.time.LocalDate

data class AdvancePaymentSearchRequestDTO (
    val paymentId: Long? = null,
    val projectIdentifiers: Set<String> = emptySet(),
    val projectAcronym: String? = null,
    val fundIds: Set<Long> = emptySet(),
    val amountFrom: BigDecimal? = null,
    val amountTo: BigDecimal? = null,
    val dateFrom: LocalDate? = null,
    val dateTo: LocalDate? = null,
    val authorized: Boolean? = null,
    val confirmed: Boolean? = null,
)
