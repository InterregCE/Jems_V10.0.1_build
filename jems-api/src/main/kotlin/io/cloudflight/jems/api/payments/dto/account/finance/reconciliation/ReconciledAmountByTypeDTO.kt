package io.cloudflight.jems.api.payments.dto.account.finance.reconciliation

import java.math.BigDecimal

data class ReconciledAmountByTypeDTO(
    val scenario4Sum: BigDecimal,
    val scenario3Sum: BigDecimal,
    val clericalMistakesSum: BigDecimal,
    val comment: String,
)
