package io.cloudflight.jems.server.project.service.partner.model

import io.cloudflight.jems.api.project.dto.InputTranslation
import java.math.BigDecimal

data class BudgetStaffCostEntry(
    override val id: Long? = null,
    override val numberOfUnits: BigDecimal,
    override val pricePerUnit: BigDecimal,
    override val rowSum: BigDecimal? = null,
    val unitType: StaffCostUnitType,
    val type: StaffCostType,
    val description: Set<InputTranslation> = emptySet(),
    val comment: Set<InputTranslation> = emptySet()
) : BaseBudgetEntry
