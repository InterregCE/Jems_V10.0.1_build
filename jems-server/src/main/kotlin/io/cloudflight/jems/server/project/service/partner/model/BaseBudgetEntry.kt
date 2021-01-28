package io.cloudflight.jems.server.project.service.partner.model

import io.cloudflight.jems.server.project.service.partner.budget.truncate
import java.math.BigDecimal

fun BaseBudgetEntry.truncateBaseEntryNumbers() = this.apply {
    numberOfUnits.truncate()
    rowSum?.truncate()
    budgetPeriods.map { it.amount.truncate() }
}

interface BaseBudgetEntry {
    val id: Long?
    val numberOfUnits: BigDecimal
    val rowSum: BigDecimal?
    val budgetPeriods: MutableSet<BudgetPeriod>
}
