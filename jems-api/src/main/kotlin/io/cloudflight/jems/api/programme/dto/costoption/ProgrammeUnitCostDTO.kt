package io.cloudflight.jems.api.programme.dto.costoption

import java.math.BigDecimal

data class ProgrammeUnitCostDTO(
    val id: Long? = null,
    val name: String? = null,
    val description: String? = null,
    val type: String? = null,
    val costPerUnit: BigDecimal? = null,
    val categories: Set<BudgetCategory> = emptySet()
)
