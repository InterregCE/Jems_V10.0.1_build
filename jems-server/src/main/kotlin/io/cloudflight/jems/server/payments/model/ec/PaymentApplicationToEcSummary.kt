package io.cloudflight.jems.server.payments.model.ec

import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import java.math.BigDecimal
import java.time.LocalDate

data class PaymentApplicationToEcSummary (
    val programmeFund: ProgrammeFund,
    val accountingYear: AccountingYear,
    val nationalReference: String?,
    val technicalAssistanceEur: BigDecimal,
    val submissionToSfcDate: LocalDate?,
    val sfcNumber: String?,
    val comment: String?
)

