package io.cloudflight.jems.server.programme.service.costoption.model

import io.cloudflight.jems.api.programme.dto.costoption.BudgetCategory
import java.math.BigDecimal

data class ProgrammeUnitCost(
    val id: Long? = null,
    val name: String? = null,
    val description: String? = null,
    val type: String? = null,
    val costPerUnit: BigDecimal? = null,
    val categories: Set<BudgetCategory> = emptySet(),
)
