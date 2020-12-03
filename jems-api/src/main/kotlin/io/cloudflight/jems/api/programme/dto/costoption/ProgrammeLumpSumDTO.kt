package io.cloudflight.jems.api.programme.dto.costoption

import java.math.BigDecimal

data class ProgrammeLumpSumDTO(

    val id: Long? = null,
    val name: String? = null,
    val description: String? = null,
    val cost: BigDecimal? = null,
    val splittingAllowed: Boolean,
    val phase: ProgrammeLumpSumPhase? = null,
    val categories: Set<BudgetCategory> = emptySet(),
)
