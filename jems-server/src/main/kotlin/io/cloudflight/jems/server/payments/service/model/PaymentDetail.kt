package io.cloudflight.jems.server.payments.service.model

import java.math.BigDecimal
import java.time.ZonedDateTime

data class PaymentDetail(
    val id: Long,
    val paymentType: PaymentType,
    val fundName: String,
    val projectCustomIdentifier: String,
    val projectAcronym: String,

    val amountApprovedPerFund: BigDecimal,
    val dateOfLastPayment: ZonedDateTime? = null,

    val partnerPayments: List<PartnerPayment>
)
