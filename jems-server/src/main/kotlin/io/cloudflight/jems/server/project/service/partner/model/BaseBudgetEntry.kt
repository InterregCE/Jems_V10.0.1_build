package io.cloudflight.jems.server.project.service.partner.model

import io.cloudflight.jems.server.project.service.partner.budget.truncate
import java.math.BigDecimal

fun BaseBudgetEntry.truncateNumbers() = this.apply {
    numberOfUnits.truncate()
    pricePerUnit.truncate()
    rowSum.truncate()
}

interface BaseBudgetEntry {
    val id: Long?
    val numberOfUnits: BigDecimal
    val pricePerUnit: BigDecimal
    val rowSum: BigDecimal
}
