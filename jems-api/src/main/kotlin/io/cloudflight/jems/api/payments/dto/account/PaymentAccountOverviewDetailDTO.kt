package io.cloudflight.jems.api.payments.dto.account

import io.cloudflight.jems.api.payments.dto.applicationToEc.AccountingYearDTO
import java.math.BigDecimal
import java.time.LocalDate

data class PaymentAccountOverviewDetailDTO(
    val id: Long,
    val accountingYear: AccountingYearDTO,
    val status: PaymentAccountStatusDTO,
    val totalEligibleExpenditure: BigDecimal,
    val nationalReference: String,
    val technicalAssistance: BigDecimal,
    val totalPublicContribution: BigDecimal,
    val totalClaimInclTA: BigDecimal,
    val submissionToSfcDate: LocalDate?,
    val sfcNumber: String,
)
