package io.cloudflight.jems.api.project.dto.partner.budget

import io.cloudflight.jems.api.project.dto.InputTranslation
import io.swagger.annotations.ApiModel
import java.math.BigDecimal

@ApiModel(value = "BudgetStaffCostEntryDTO", parent = BaseBudgetEntryDTO::class)
data class BudgetStaffCostEntryDTO(
    override val id: Long? = null,
    override val numberOfUnits: BigDecimal,
    override val pricePerUnit: BigDecimal,
    override val rowSum: BigDecimal? = null,
    val unitType: String,
    val type: String,
    val description: Set<InputTranslation> = emptySet(),
    val comment: Set<InputTranslation> = emptySet()
) : BaseBudgetEntryDTO
