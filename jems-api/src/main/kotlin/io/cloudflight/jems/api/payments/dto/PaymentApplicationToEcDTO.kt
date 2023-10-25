package io.cloudflight.jems.api.payments.dto

import io.cloudflight.jems.api.payments.dto.applicationToEc.AccountingYearDTO
import io.cloudflight.jems.api.programme.dto.fund.ProgrammeFundDTO

data class PaymentApplicationToEcDTO (
    val id: Long,
    val programmeFund: ProgrammeFundDTO,
    val accountingYear: AccountingYearDTO,
    val status: PaymentEcStatusDTO
)
