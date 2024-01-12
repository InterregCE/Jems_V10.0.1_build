package io.cloudflight.jems.server.payments.model.account

import io.cloudflight.jems.server.payments.model.ec.AccountingYear
import java.math.BigDecimal
import java.time.LocalDate

data class PaymentAccountOverviewDetail(
    val id: Long,
    val accountingYear: AccountingYear,
    val status: PaymentAccountStatus,
    val totalEligibleExpenditure: BigDecimal,
    val nationalReference: String,
    val technicalAssistance: BigDecimal,
    val totalPublicContribution: BigDecimal,
    val totalClaimInclTA: BigDecimal,
    val submissionToSfcDate: LocalDate?,
    val sfcNumber: String,
)
