package io.cloudflight.jems.api.project.dto.partner.budget

import io.cloudflight.jems.api.project.dto.InputTranslation
import io.swagger.annotations.ApiModel
import java.math.BigDecimal

@ApiModel(value = "BudgetStaffCostEntryDTO", parent = BaseBudgetEntryDTO::class)
data class BudgetStaffCostEntryDTO(
    override val id: Long? = null,
    override val numberOfUnits: BigDecimal,
    override val rowSum: BigDecimal?,
    override val budgetPeriods: Set<BudgetPeriodDTO> = emptySet(),
    val pricePerUnit: BigDecimal,
    val unitType: StaffCostUnitTypeDTO? = null,
    val type: StaffCostTypeDTO? = null,
    val description: Set<InputTranslation> = emptySet(),
    val comment: Set<InputTranslation> = emptySet()
) : BaseBudgetEntryDTO
