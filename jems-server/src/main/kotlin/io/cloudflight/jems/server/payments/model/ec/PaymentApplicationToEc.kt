package io.cloudflight.jems.server.payments.model.ec

import io.cloudflight.jems.server.payments.model.regular.PaymentEcStatus
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund

data class PaymentApplicationToEc(
    val id: Long,
    val programmeFund: ProgrammeFund,
    val accountingYear: AccountingYear,
    val status: PaymentEcStatus
)
