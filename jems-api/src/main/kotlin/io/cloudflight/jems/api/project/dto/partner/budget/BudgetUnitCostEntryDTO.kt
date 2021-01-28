package io.cloudflight.jems.api.project.dto.partner.budget

import io.swagger.annotations.ApiModel
import java.math.BigDecimal

@ApiModel(value = "BudgetUnitCostEntryDTO", parent = BaseBudgetEntryDTO::class)
data class BudgetUnitCostEntryDTO(
    override val id: Long? = null,
    override val numberOfUnits: BigDecimal,
    override val rowSum: BigDecimal?,
    override val budgetPeriods: Set<BudgetPeriodDTO> = emptySet(),
    val unitCostId: Long
) : BaseBudgetEntryDTO
