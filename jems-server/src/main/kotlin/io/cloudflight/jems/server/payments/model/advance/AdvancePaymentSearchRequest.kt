package io.cloudflight.jems.server.payments.model.advance

import java.math.BigDecimal
import java.time.LocalDate

data class AdvancePaymentSearchRequest (
    val paymentId: Long?,
    val projectIdentifiers: Set<String>,
    val projectAcronym: String?,
    val fundIds: Set<Long>,
    val amountFrom: BigDecimal?,
    val amountTo: BigDecimal?,
    val dateFrom: LocalDate?,
    val dateTo: LocalDate?,
    val authorized: Boolean?,
    val confirmed: Boolean?,
)
