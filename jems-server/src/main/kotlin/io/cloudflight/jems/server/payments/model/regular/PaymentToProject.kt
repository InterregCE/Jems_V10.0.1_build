package io.cloudflight.jems.server.payments.model.regular

import java.math.BigDecimal
import java.time.LocalDate
import java.time.ZonedDateTime

data class PaymentToProject(
    val id: Long,
    val paymentType: PaymentType,
    val projectId: Long,
    val projectCustomIdentifier: String,
    val projectAcronym: String,
    val paymentClaimId: Long?,
    val paymentClaimNo: Int = 0,
    val paymentClaimSubmissionDate: ZonedDateTime?,
    val lumpSumId: Long? = null,
    val orderNr: Int? = null,
    val paymentApprovalDate: ZonedDateTime?,
    val totalEligibleAmount: BigDecimal,
    val fundId: Long,
    val fundName: String,
    val amountApprovedPerFund: BigDecimal,
    val amountPaidPerFund: BigDecimal,
    val amountAuthorizedPerFund: BigDecimal,
    val dateOfLastPayment: LocalDate? = null,
    val lastApprovedVersionBeforeReadyForPayment: String?,
)
