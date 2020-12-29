package io.cloudflight.jems.server.project.service.partner.model

import io.cloudflight.jems.api.project.dto.InputTranslation
import java.math.BigDecimal

data class BudgetGeneralCostEntry(
    override val id: Long? = null,
    override val numberOfUnits: BigDecimal,
    override val pricePerUnit: BigDecimal,
    override val rowSum: BigDecimal? = null,
    val investmentId: Long? = null,
    val unitType: Set<InputTranslation> = emptySet(),
    val awardProcedures: Set<InputTranslation> = emptySet(),
    val description: Set<InputTranslation> = emptySet(),
) : BaseBudgetEntry
