package io.cloudflight.jems.server.payments.model.regular

import java.math.BigDecimal
import java.time.ZonedDateTime

data class PaymentDetail(
    val id: Long,
    val paymentType: PaymentType,
    val fundName: String,
    val projectId: Long,
    val projectCustomIdentifier: String,
    val projectAcronym: String,

    val amountApprovedPerFund: BigDecimal,
    val dateOfLastPayment: ZonedDateTime? = null,

    val partnerPayments: List<PartnerPayment>
)
