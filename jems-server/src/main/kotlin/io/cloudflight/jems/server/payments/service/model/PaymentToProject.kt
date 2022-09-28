package io.cloudflight.jems.server.payments.service.model

import java.math.BigDecimal
import java.time.ZonedDateTime

data class PaymentToProject (
    val id: Long,
    val paymentType: PaymentType,
    val projectId: String,
    val projectAcronym: String,
    val paymentClaimNo: Int = 0,
    val paymentClaimSubmissionDate: ZonedDateTime?,
    val paymentApprovalDate: ZonedDateTime?,
    val totalEligibleAmount: BigDecimal,
    val fundName: String,
    val amountApprovedPerFund: BigDecimal,
    val amountPaidPerFund: BigDecimal,
    val dateOfLastPayment: ZonedDateTime? = null,
    val lastApprovedVersionBeforeReadyForPayment: String?
)
