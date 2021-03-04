package io.cloudflight.jems.server.programme.service.costoption.model

import io.cloudflight.jems.api.programme.dto.costoption.BudgetCategory
import io.cloudflight.jems.api.programme.dto.costoption.ProgrammeLumpSumPhase
import io.cloudflight.jems.api.project.dto.InputTranslation
import java.math.BigDecimal

data class ProgrammeLumpSum(
    val id: Long = 0,
    val name: Set<InputTranslation> = emptySet(),
    val description: Set<InputTranslation> = emptySet(),
    val cost: BigDecimal? = null,
    val splittingAllowed: Boolean,
    val phase: ProgrammeLumpSumPhase? = null,
    val categories: Set<BudgetCategory> = emptySet(),
)
