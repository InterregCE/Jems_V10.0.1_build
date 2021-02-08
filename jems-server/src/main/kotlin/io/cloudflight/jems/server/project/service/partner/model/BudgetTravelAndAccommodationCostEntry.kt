package io.cloudflight.jems.server.project.service.partner.model

import io.cloudflight.jems.api.project.dto.InputTranslation
import java.math.BigDecimal

class BudgetTravelAndAccommodationCostEntry(
    override val id: Long? = null,
    override val numberOfUnits: BigDecimal,
    override var rowSum: BigDecimal?,
    override val budgetPeriods: MutableSet<BudgetPeriod>,
    val pricePerUnit: BigDecimal,
    val unitType: Set<InputTranslation> = emptySet(),
    val description: Set<InputTranslation> = emptySet(),
) : BaseBudgetEntry
