package io.cloudflight.jems.server.payments.model.account

import io.cloudflight.jems.server.payments.model.ec.AccountingYear
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import java.math.BigDecimal
import java.time.LocalDate

data class PaymentAccount(
    val id: Long,
    val fund: ProgrammeFund,
    val accountingYear: AccountingYear,
    val status: PaymentAccountStatus,
    val nationalReference: String,
    val technicalAssistance: BigDecimal,
    val submissionToSfcDate: LocalDate?,
    val sfcNumber: String,
    val comment: String
)
