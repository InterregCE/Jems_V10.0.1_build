package io.cloudflight.jems.server.payments.model.account

import java.math.BigDecimal

data class PaymentAccountAmountSummaryLine(
    val priorityAxis: String?,
    val totalEligibleExpenditure: BigDecimal,
    val totalPublicContribution: BigDecimal
)
