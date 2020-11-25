package io.cloudflight.jems.api.project.dto.partner.budget

import io.cloudflight.jems.api.project.dto.InputTranslation
import io.swagger.annotations.ApiModel
import java.math.BigDecimal

/**
 * Input for Equipment, External and Infrastructure which will differ from Staff and Travel budget.
 */
@ApiModel(value = "InputGeneralBudget", parent = InputBudget::class)
data class InputGeneralBudget(
    override val id: Long? = null,
    override val numberOfUnits: BigDecimal,
    override val pricePerUnit: BigDecimal,
    override val rowSum: BigDecimal? = null,
    val description: Set<InputTranslation> = emptySet()
) : InputBudget
