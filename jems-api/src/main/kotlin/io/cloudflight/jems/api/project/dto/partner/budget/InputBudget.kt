package io.cloudflight.jems.api.project.dto.partner.budget

import io.swagger.annotations.ApiModel
import java.math.BigDecimal

@ApiModel(
    value = "InputBudget",
    subTypes = [InputGeneralBudget::class, InputStaffCostBudget::class, InputTravelBudget::class]
)
interface InputBudget {
    val id: Long?
    val numberOfUnits: BigDecimal
    val pricePerUnit: BigDecimal
    val rowSum: BigDecimal?
}
