package io.cloudflight.jems.api.project.dto.partner.budget

import io.swagger.annotations.ApiModel
import java.math.BigDecimal

@ApiModel(
    value = "BaseBudgetEntryDTO",
    subTypes = [
        BudgetGeneralCostEntryDTO::class,
        BudgetStaffCostEntryDTO::class,
        BudgetTravelAndAccommodationCostEntryDTO::class,
        BudgetSpfCostEntryDTO::class
    ]
)
interface BaseBudgetEntryDTO {
    val id: Long?
    val numberOfUnits: BigDecimal
    val rowSum: BigDecimal?
    val budgetPeriods: Set<BudgetPeriodDTO>
}
