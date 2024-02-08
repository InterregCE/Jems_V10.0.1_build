package io.cloudflight.jems.server.payments.model.account.finance.reconciliation

import java.math.BigDecimal

data class ReconciledPriority(
    val priorityId: Long,
    val priorityCode: String,

    val total: BigDecimal,
    val ofWhichAa: BigDecimal,
    val ofWhichEc: BigDecimal,
)
