package io.cloudflight.jems.api.project.dto.partner.budget

import io.cloudflight.jems.api.project.dto.InputTranslation
import io.swagger.annotations.ApiModel
import java.math.BigDecimal

/**
 * Input for Travel which will differ from Staff and Other budget.
 */
@ApiModel(value = "InputTravelBudget", parent = InputBudget::class)
data class InputTravelBudget(
    override val id: Long? = null,
    override val numberOfUnits: BigDecimal,
    override val pricePerUnit: BigDecimal,
    override val rowSum: BigDecimal? = null,
    val description: Set<InputTranslation> = emptySet()
) : InputBudget
