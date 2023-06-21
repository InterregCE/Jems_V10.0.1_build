package io.cloudflight.jems.api.payments.dto

import java.math.BigDecimal
import java.time.LocalDate
import java.time.ZonedDateTime

data class PaymentToProjectDTO(
    val id: Long,
    val paymentType: PaymentTypeDTO,
    val projectCustomIdentifier: String,
    val projectAcronym: String,
    val paymentClaimNo: Int = 0,
    val paymentClaimSubmissionDate: ZonedDateTime?,
    val paymentApprovalDate: ZonedDateTime?,
    val totalEligibleAmount: BigDecimal,
    val fundName: String,
    val amountApprovedPerFund: BigDecimal,
    val amountPaidPerFund: BigDecimal,
    val dateOfLastPayment: LocalDate? = null,
    val lastApprovedVersionBeforeReadyForPayment: String?
)
