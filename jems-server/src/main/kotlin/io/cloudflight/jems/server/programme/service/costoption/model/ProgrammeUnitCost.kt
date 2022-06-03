package io.cloudflight.jems.server.programme.service.costoption.model

import io.cloudflight.jems.api.programme.dto.costoption.BudgetCategory
import io.cloudflight.jems.api.project.dto.InputTranslation
import java.math.BigDecimal

data class ProgrammeUnitCost(
    val id: Long = 0,
    val name: Set<InputTranslation> = emptySet(),
    val description: Set<InputTranslation> = emptySet(),
    val type: Set<InputTranslation> = emptySet(),
    val costPerUnit: BigDecimal? = null,
    val costPerUnitForeignCurrency: BigDecimal? = null,
    val foreignCurrencyCode: String? = null,
    val isOneCostCategory: Boolean,
    val categories: Set<BudgetCategory> = emptySet()
): Comparable<ProgrammeUnitCost> {

    override fun compareTo(other: ProgrammeUnitCost) = when {
        isOneCostCategory != other.isOneCostCategory -> if (isOneCostCategory) 1 else -1
        categories.first() != other.categories.first() -> categories.first().compareTo(other.categories.first())
        else -> if (id > other.id) 1 else if (id < other.id) -1 else 0
    }

    fun isMultipleCategoryUnitCost() = !isOneCostCategory

}
