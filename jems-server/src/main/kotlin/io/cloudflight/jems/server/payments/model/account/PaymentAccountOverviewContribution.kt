package io.cloudflight.jems.server.payments.model.account

import java.math.BigDecimal

data class PaymentAccountOverviewContribution(
    val totalEligibleExpenditure: BigDecimal,
    val totalPublicContribution: BigDecimal,
)
