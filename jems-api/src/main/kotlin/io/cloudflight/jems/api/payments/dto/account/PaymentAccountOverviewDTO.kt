package io.cloudflight.jems.api.payments.dto.account

import io.cloudflight.jems.api.programme.dto.fund.ProgrammeFundDTO

data class PaymentAccountOverviewDTO(
    val programmeFund: ProgrammeFundDTO,
    val paymentAccounts: List<PaymentAccountOverviewDetailDTO>
)
