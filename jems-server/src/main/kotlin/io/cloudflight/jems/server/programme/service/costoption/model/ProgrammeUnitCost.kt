package io.cloudflight.jems.server.programme.service.costoption.model

import io.cloudflight.jems.api.programme.dto.costoption.BudgetCategory
import io.cloudflight.jems.api.project.dto.InputTranslation
import java.math.BigDecimal

data class ProgrammeUnitCost(
    val id: Long = 0,
    var projectId: Long?,
    val name: Set<InputTranslation> = emptySet(),
    val description: Set<InputTranslation> = emptySet(),
    val type: Set<InputTranslation> = emptySet(),
    val justification: Set<InputTranslation> = emptySet(),
    val costPerUnit: BigDecimal? = null,
    val costPerUnitForeignCurrency: BigDecimal? = null,
    val foreignCurrencyCode: String? = null,
    val isOneCostCategory: Boolean,
    val categories: Set<BudgetCategory> = emptySet(),
    val paymentClaim: PaymentClaim
): Comparable<ProgrammeUnitCost> {

    override fun compareTo(other: ProgrammeUnitCost) = when {
        isOneCostCategory != other.isOneCostCategory -> if (isOneCostCategory) 1 else -1
        categories.first() != other.categories.first() -> categories.first().compareTo(other.categories.first())
        else -> if (id > other.id) 1 else if (id < other.id) -1 else 0
    }

    fun isMultipleCategoryUnitCost() = !isOneCostCategory

    fun getDiff(old: ProgrammeUnitCost? = null): Map<String, Pair<Any?, Any?>> {
        val changes = mutableMapOf<String, Pair<Any?, Any?>>()

        if (name != (old?.name ?: emptySet<InputTranslation>()))
            changes["name"] = Pair(old?.name, name)

        if (description != (old?.description ?: emptySet<InputTranslation>()))
            changes["description"] = Pair(old?.description, description)

        if (type != (old?.type ?: emptySet<InputTranslation>()))
            changes["type"] = Pair(old?.type, type)

        if (justification != (old?.justification ?: emptySet<InputTranslation>()))
            changes["justification"] = Pair(old?.justification, justification)

        if ((costPerUnit?: BigDecimal.ZERO).compareTo(old?.costPerUnit ?: BigDecimal.ZERO) != 0)
            changes["costPerUnit"] = Pair(old?.costPerUnit, costPerUnit)

        if ((costPerUnitForeignCurrency?: BigDecimal.ZERO).compareTo(old?.costPerUnitForeignCurrency ?: BigDecimal.ZERO) != 0)
            changes["costPerUnitForeignCurrency"] = Pair(old?.costPerUnitForeignCurrency, costPerUnitForeignCurrency)

        if (foreignCurrencyCode != old?.foreignCurrencyCode)
            changes["foreignCurrencyCode"] = Pair(old?.foreignCurrencyCode, foreignCurrencyCode)

        if (isOneCostCategory != old?.isOneCostCategory)
            changes["isOneCostCategory"] = Pair(old?.isOneCostCategory, isOneCostCategory)

        if (categories != (old?.categories ?: emptySet<BudgetCategory>()))
            changes["categories"] = Pair(old?.categories ?: "", categories)

        if (paymentClaim != old?.paymentClaim)
            changes["paymentClaim"] = Pair(old?.paymentClaim, paymentClaim)

        return changes
    }

}
