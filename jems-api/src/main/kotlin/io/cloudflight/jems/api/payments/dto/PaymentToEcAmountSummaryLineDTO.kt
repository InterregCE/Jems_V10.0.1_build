package io.cloudflight.jems.api.payments.dto

import java.math.BigDecimal

data class PaymentToEcAmountSummaryLineDTO(
    val priorityAxis: String?,
    val totalEligibleExpenditure: BigDecimal,
    val totalUnionContribution: BigDecimal,
    val totalPublicContribution: BigDecimal
)
