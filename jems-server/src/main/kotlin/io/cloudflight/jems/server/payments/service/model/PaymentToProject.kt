package io.cloudflight.jems.server.payments.service.model

import io.cloudflight.jems.api.payments.PaymentType
import java.math.BigDecimal
import java.time.ZonedDateTime

data class PaymentToProject (
    val paymentId: Long,
    val paymentType: PaymentType,
    val projectId: String,
    val projectAcronym: String,
    val paymentClaimNo: Int = 0,
    val paymentClaimSubmissionDate: ZonedDateTime?,
    val paymentApprovalDate: ZonedDateTime?,
    val totalEligibleAmount: BigDecimal,
    val fundName: String,
    val amountApprovedPerFound: BigDecimal,
    val amountPaidPerFund: BigDecimal,
    val dateOfLastPayment: ZonedDateTime? = null
)
