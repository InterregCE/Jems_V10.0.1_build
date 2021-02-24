package io.cloudflight.jems.api.programme.dto.costoption

import io.cloudflight.jems.api.project.dto.InputTranslation
import java.math.BigDecimal

data class ProgrammeLumpSumDTO(

    val id: Long? = null,
    val name: Set<InputTranslation> = emptySet(),
    val description: Set<InputTranslation> = emptySet(),
    val cost: BigDecimal? = null,
    val splittingAllowed: Boolean,
    val phase: ProgrammeLumpSumPhase? = null,
    val categories: Set<BudgetCategory> = emptySet()
)
