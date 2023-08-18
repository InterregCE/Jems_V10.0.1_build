package io.cloudflight.jems.server.payments.model.ec

import io.cloudflight.jems.server.payments.model.regular.AccountingYear
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund

data class PaymentApplicationToEcSummary (
    val programmeFund: ProgrammeFund,
    val accountingYear: AccountingYear
)

