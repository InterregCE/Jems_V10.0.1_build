package io.cloudflight.jems.server.payments.model.account.finance.reconciliation

data class ReconciledAmountPerPriority(
    val priorityAxis: String,

    val reconciledAmountTotal: ReconciledAmountByType,
    val reconciledAmountOfAa: ReconciledAmountByType,
    val reconciledAmountOfEc: ReconciledAmountByType,
)
