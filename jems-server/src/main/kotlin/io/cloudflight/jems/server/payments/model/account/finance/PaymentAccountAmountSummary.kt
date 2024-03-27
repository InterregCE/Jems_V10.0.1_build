package io.cloudflight.jems.server.payments.model.account.finance

data class PaymentAccountAmountSummary(
    val amountsGroupedByPriority: List<PaymentAccountAmountSummaryLine>,
    val totals: PaymentAccountAmountSummaryLine
)
