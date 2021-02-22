package io.cloudflight.jems.server.programme.service.costoption.model

import io.cloudflight.jems.api.programme.dto.costoption.BudgetCategory
import io.cloudflight.jems.api.project.dto.InputTranslation
import java.math.BigDecimal

data class ProgrammeUnitCost(
    val id: Long? = null,
    val name: Set<InputTranslation> = emptySet(),
    val description: Set<InputTranslation> = emptySet(),
    val type: Set<InputTranslation> = emptySet(),
    val costPerUnit: BigDecimal? = null,
    val isOneCostCategory: Boolean,
    val categories: Set<BudgetCategory> = emptySet()
)
