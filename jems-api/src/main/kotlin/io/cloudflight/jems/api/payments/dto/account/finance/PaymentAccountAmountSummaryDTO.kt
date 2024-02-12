package io.cloudflight.jems.api.payments.dto.account.finance

data class PaymentAccountAmountSummaryDTO(
    val amountsGroupedByPriority: List<PaymentAccountAmountSummaryLineDTO>,
    val totals: PaymentAccountAmountSummaryLineDTO
)
