package io.cloudflight.jems.server.payments.model.account.finance.reconciliation

data class ReconciledAmountUpdate(
    val priorityAxisId: Long,
    val type: PaymentAccountReconciliationType,
    val comment: String
)
