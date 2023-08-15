package io.cloudflight.jems.server.payments.model.ec

import io.cloudflight.jems.server.payments.model.regular.AccountingYear
import io.cloudflight.jems.server.payments.model.regular.PaymentEcStatus
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund

data class PaymentApplicationsToEc(
    val id: Long,
    val programmeFund: ProgrammeFund,
    val accountingYear: AccountingYear,
    val status: PaymentEcStatus
)
