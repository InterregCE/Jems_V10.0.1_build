package io.cloudflight.jems.server.payments.model.regular

import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import java.math.BigDecimal
import java.time.ZonedDateTime

data class PaymentDetail(
    val id: Long,
    val paymentType: PaymentType,
    val fund: ProgrammeFund,
    val projectId: Long,
    val projectCustomIdentifier: String,
    val projectAcronym: String,
    val spf: Boolean,

    val amountApprovedPerFund: BigDecimal,
    val dateOfLastPayment: ZonedDateTime? = null,

    val partnerPayments: List<PartnerPayment>
)
