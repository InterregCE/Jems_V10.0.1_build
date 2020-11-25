package io.cloudflight.jems.api.project.dto.partner.budget

import io.cloudflight.jems.api.project.dto.InputTranslation
import io.swagger.annotations.ApiModel
import java.math.BigDecimal

/**
 * Input for StaffCost which will differ from Travel and Other budget.
 */
@ApiModel(value = "InputStaffCostBudget", parent = InputBudget::class)
data class InputStaffCostBudget(
    override val id: Long? = null,
    override val numberOfUnits: BigDecimal,
    override val pricePerUnit: BigDecimal,
    override val rowSum: BigDecimal? = null,
    val description: Set<InputTranslation> = emptySet()
) : InputBudget
