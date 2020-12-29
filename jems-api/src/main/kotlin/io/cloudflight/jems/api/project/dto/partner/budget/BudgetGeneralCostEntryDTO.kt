package io.cloudflight.jems.api.project.dto.partner.budget

import io.cloudflight.jems.api.project.dto.InputTranslation
import io.swagger.annotations.ApiModel
import java.math.BigDecimal
import java.util.*


@ApiModel(value = "BudgetGeneralCostEntryDTO", parent = BaseBudgetEntryDTO::class)
data class BudgetGeneralCostEntryDTO(
    override val id: Long? = null,
    override val numberOfUnits: BigDecimal,
    override val pricePerUnit: BigDecimal,
    override val rowSum: BigDecimal? = null,
    val investmentId: Long? = null,
    val unitType: Set<InputTranslation> = emptySet(),
    val awardProcedures: Set<InputTranslation> = emptySet(),
    val description: Set<InputTranslation> = emptySet()
) : BaseBudgetEntryDTO
