package io.cloudflight.jems.api.payments.dto

import java.math.BigDecimal
import java.time.LocalDate
import java.time.ZonedDateTime

data class PaymentToProjectDTO(
    val id: Long,
    val paymentType: PaymentTypeDTO,
    val projectId: Long,
    val projectCustomIdentifier: String,
    val projectAcronym: String,
    val paymentClaimId: Long?,
    val paymentClaimNo: Int = 0,
    val paymentClaimSubmissionDate: ZonedDateTime?,
    val paymentToEcId: Long?,
    val paymentApprovalDate: ZonedDateTime?,
    val totalEligibleAmount: BigDecimal,
    val fundName: String,
    val fundAmount: BigDecimal,
    val amountPaidPerFund: BigDecimal,
    val amountAuthorizedPerFund: BigDecimal,
    val dateOfLastPayment: LocalDate? = null,
    val lastApprovedVersionBeforeReadyForPayment: String?,
    val remainingToBePaid: BigDecimal,
)
