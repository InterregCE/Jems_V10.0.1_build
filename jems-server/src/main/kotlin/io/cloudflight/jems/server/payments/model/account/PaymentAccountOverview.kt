package io.cloudflight.jems.server.payments.model.account

import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund

data class PaymentAccountOverview(
    val programmeFund: ProgrammeFund,
    val paymentAccounts: List<PaymentAccountOverviewDetail>
)
