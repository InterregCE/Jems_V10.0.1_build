package io.cloudflight.jems.server.project.service.partner.model

import io.cloudflight.jems.api.project.dto.InputTranslation
import java.math.BigDecimal

data class BudgetStaffCostEntry(
    override val id: Long? = null,
    override val numberOfUnits: BigDecimal,
    override var rowSum: BigDecimal?,
    override val budgetPeriods: MutableSet<BudgetPeriod>,
    override val unitCostId: Long?,
    var pricePerUnit: BigDecimal,
    val description: Set<InputTranslation> = emptySet(),
    val comments: Set<InputTranslation> = emptySet(),
    var unitType: Set<InputTranslation> = emptySet(),
) : BaseBudgetEntry
