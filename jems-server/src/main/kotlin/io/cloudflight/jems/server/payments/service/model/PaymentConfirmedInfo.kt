package io.cloudflight.jems.server.payments.service.model

import java.math.BigDecimal
import java.time.LocalDate

data class PaymentConfirmedInfo (
    val id: Long,
    val amountPaidPerFund: BigDecimal,
    val dateOfLastPayment: LocalDate? = null
)
