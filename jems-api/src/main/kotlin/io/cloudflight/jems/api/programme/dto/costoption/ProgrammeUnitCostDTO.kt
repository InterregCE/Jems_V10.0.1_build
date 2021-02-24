package io.cloudflight.jems.api.programme.dto.costoption

import io.cloudflight.jems.api.project.dto.InputTranslation
import java.math.BigDecimal

data class ProgrammeUnitCostDTO(
    val id: Long? = null,
    val name: Set<InputTranslation> = emptySet(),
    val description: Set<InputTranslation> = emptySet(),
    val type: Set<InputTranslation> = emptySet(),
    val costPerUnit: BigDecimal? = null,
    val isOneCostCategory: Boolean? = false,
    val categories: Set<BudgetCategory> = emptySet()
)
