package io.cloudflight.jems.api.payments.dto

data class PaymentToEcAmountSummaryDTO(
    val amountsGroupedByPriority: List<PaymentToEcAmountSummaryLineDTO>,
    val totals: PaymentToEcAmountSummaryLineDTO
)
