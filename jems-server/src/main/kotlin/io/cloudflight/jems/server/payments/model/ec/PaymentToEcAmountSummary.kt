package io.cloudflight.jems.server.payments.model.ec

data class PaymentToEcAmountSummary(
    val amountsGroupedByPriority: List<PaymentToEcAmountSummaryLine>,
    val totals: PaymentToEcAmountSummaryLine
)
