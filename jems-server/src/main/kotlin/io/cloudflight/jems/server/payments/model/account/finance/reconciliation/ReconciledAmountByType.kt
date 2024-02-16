package io.cloudflight.jems.server.payments.model.account.finance.reconciliation

import java.math.BigDecimal

data class ReconciledAmountByType(
    val scenario4Sum: BigDecimal,
    val scenario3Sum: BigDecimal,
    val clericalMistakesSum: BigDecimal,
    val comment: String,
)
