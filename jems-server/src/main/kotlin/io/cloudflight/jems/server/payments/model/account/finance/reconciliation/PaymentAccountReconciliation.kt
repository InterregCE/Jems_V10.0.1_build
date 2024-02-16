package io.cloudflight.jems.server.payments.model.account.finance.reconciliation

import io.cloudflight.jems.server.payments.model.account.PaymentAccount

data class PaymentAccountReconciliation(
    val id: Long,
    val paymentAccount: PaymentAccount,
    val priorityAxisId: Long,
    val totalComment: String,
    val aaComment: String,
    val ecComment: String
)
