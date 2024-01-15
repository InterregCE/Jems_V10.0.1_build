package io.cloudflight.jems.api.payments.dto

import io.cloudflight.jems.api.payments.dto.applicationToEc.AccountingYearDTO
import io.cloudflight.jems.api.programme.dto.fund.ProgrammeFundDTO
import java.math.BigDecimal
import java.time.LocalDate

data class PaymentApplicationToEcSummaryDTO (
    val programmeFund: ProgrammeFundDTO,
    val accountingYear: AccountingYearDTO,
    val nationalReference: String?,
    val technicalAssistanceEur: BigDecimal,
    val submissionToSfcDate: LocalDate?,
    val sfcNumber: String?,
    val comment: String?
)
