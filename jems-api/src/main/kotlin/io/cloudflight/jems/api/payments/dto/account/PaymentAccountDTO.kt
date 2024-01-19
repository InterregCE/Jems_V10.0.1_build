package io.cloudflight.jems.api.payments.dto.account

import io.cloudflight.jems.api.payments.dto.applicationToEc.AccountingYearDTO
import io.cloudflight.jems.api.programme.dto.fund.ProgrammeFundDTO
import java.math.BigDecimal
import java.time.LocalDate

data class PaymentAccountDTO(
    val id: Long,
    val fund: ProgrammeFundDTO,
    val accountingYear: AccountingYearDTO,
    val status: PaymentAccountStatusDTO,
    val nationalReference: String,
    val technicalAssistance: BigDecimal,
    val submissionToSfcDate: LocalDate?,
    val sfcNumber: String,
    val comment: String
)
