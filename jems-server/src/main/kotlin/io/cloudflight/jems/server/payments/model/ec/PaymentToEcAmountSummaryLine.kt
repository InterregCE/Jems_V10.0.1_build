package io.cloudflight.jems.server.payments.model.ec

import java.math.BigDecimal

data class PaymentToEcAmountSummaryLine(
    val priorityAxis: String?,
    val totalEligibleExpenditure: BigDecimal,
    val totalUnionContribution: BigDecimal,
    val totalPublicContribution: BigDecimal
)
