package io.cloudflight.jems.server.payments.service.model

import java.math.BigDecimal
import java.time.ZonedDateTime

data class PaymentDetail(
    val id: Long,
    val paymentType: PaymentType,
    val projectId: Long,
    val fundName: String,

    val projectAcronym: String,
    val amountApprovedPerFund: BigDecimal,
    val dateOfLastPayment: ZonedDateTime? = null,

    val partnerPayments: List<PartnerPayment>
)
