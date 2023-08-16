package io.cloudflight.jems.api.payments.dto

import io.cloudflight.jems.api.accountingYear.AccountingYearDTO
import io.cloudflight.jems.api.programme.dto.fund.ProgrammeFundDTO

data class PaymentApplicationToEcSummaryDTO (
    val programmeFund: ProgrammeFundDTO,
    val accountingYear: AccountingYearDTO
)
