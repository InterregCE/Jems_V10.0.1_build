package io.cloudflight.jems.server.payments.model.regular

import java.math.BigDecimal
import java.time.ZonedDateTime

data class PaymentToProject (
    val id: Long,
    val paymentType: PaymentType,
    val projectCustomIdentifier: String,
    val projectAcronym: String,
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
    val dateOfLastPayment: ZonedDateTime? = null,
    val lastApprovedVersionBeforeReadyForPayment: String?
)
