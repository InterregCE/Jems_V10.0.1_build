package io.cloudflight.jems.server.project.service.partner.model

import java.math.BigDecimal

data class BudgetUnitCostEntry(
    override val id: Long? = null,
    override val numberOfUnits: BigDecimal,
    override val budgetPeriods: MutableSet<BudgetPeriod>,
    override var rowSum: BigDecimal?,
    override val unitCostId: Long
) : BaseBudgetEntry
