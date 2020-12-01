package io.cloudflight.jems.server.programme.service.costoption.model

import io.cloudflight.jems.api.programme.dto.costoption.BudgetCategory
import io.cloudflight.jems.api.programme.dto.costoption.ProgrammeLumpSumPhase
import java.math.BigDecimal

data class ProgrammeLumpSum(
    val id: Long? = null,
    val name: String? = null,
    val description: String? = null,
    val cost: BigDecimal? = null,
    val splittingAllowed: Boolean,
    val phase: ProgrammeLumpSumPhase? = null,
    val categories: Set<BudgetCategory> = emptySet(),
)
