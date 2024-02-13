package io.cloudflight.jems.api.payments.dto.account.finance

import java.math.BigDecimal

data class PaymentAccountAmountSummaryLineDTO(
    val priorityAxis: String?,
    val totalEligibleExpenditure: BigDecimal,
    val totalPublicContribution: BigDecimal
)
