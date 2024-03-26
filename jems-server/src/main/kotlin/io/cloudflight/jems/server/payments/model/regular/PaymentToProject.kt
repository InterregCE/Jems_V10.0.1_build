package io.cloudflight.jems.server.payments.model.regular

import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
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
    val paymentToEcId: Long?,
    val lumpSumId: Long? = null,
    val orderNr: Int? = null,
    val paymentApprovalDate: ZonedDateTime?,
    val totalEligibleAmount: BigDecimal,
    val fund: ProgrammeFund,
    val fundAmount: BigDecimal,
    val amountPaidPerFund: BigDecimal,
    val amountAuthorizedPerFund: BigDecimal,
    val dateOfLastPayment: LocalDate?,
    val lastApprovedVersionBeforeReadyForPayment: String?,
    val remainingToBePaid: BigDecimal,
)
