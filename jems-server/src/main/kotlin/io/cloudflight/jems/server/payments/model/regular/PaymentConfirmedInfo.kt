package io.cloudflight.jems.server.payments.model.regular

import java.math.BigDecimal
import java.time.LocalDate

data class PaymentConfirmedInfo (
    val id: Long,
    val amountPaidPerFund: BigDecimal,
    val amountAuthorizedPerFund: BigDecimal,
    val dateOfLastPayment: LocalDate? = null
)
