package io.cloudflight.jems.api.project.dto.partner.budget

import java.math.BigDecimal

data class BudgetUnitCostEntryDTO(
    val id: Long? = null,
    val numberOfUnits: BigDecimal,
    val unitCostId: Long? = null,
    val rowSum: BigDecimal
)
