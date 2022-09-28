package io.cloudflight.jems.api.payments

import java.math.BigDecimal
import java.time.ZonedDateTime

data class PaymentToProjectDTO(
    val id: Long,
    val paymentType: PaymentTypeDTO,
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
