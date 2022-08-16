package io.cloudflight.jems.api.payments

import java.time.ZonedDateTime

data class PaymentToProjectDTO (
    val paymentId: Long,
    val paymentType: PaymentType,
    val projectId: Long,
    val projectAcronym: String,
    val paymentClaimNo: Int = 0,
    val paymentClaimSubmissionDate: ZonedDateTime?,
    val paymentApprovalDate: ZonedDateTime?,
    val totalEligibleAmount: Double,
    val fundName: String,
    val amountApprovedPerFound: Double,
    val amountPaidPerFund: Double,
    val dateOfLastPayment: ZonedDateTime? = null
)
