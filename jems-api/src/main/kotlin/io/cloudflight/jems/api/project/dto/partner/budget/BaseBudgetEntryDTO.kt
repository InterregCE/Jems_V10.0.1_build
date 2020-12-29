package io.cloudflight.jems.api.project.dto.partner.budget

import io.swagger.annotations.ApiModel
import java.math.BigDecimal

@ApiModel(
    value = "BaseBudgetEntryDTO",
    subTypes = [BudgetGeneralCostEntryDTO::class, BudgetStaffCostEntryDTO::class, BudgetTravelAndAccommodationCostEntryDTO::class]
)
interface BaseBudgetEntryDTO {
    val id: Long?
    val numberOfUnits: BigDecimal
    val pricePerUnit: BigDecimal
    val rowSum: BigDecimal?
}
