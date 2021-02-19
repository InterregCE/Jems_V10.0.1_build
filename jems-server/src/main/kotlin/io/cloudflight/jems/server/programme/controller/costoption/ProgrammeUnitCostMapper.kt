package io.cloudflight.jems.server.programme.controller.costoption

import io.cloudflight.jems.api.programme.dto.costoption.BudgetCategory
import io.cloudflight.jems.api.programme.dto.costoption.ProgrammeUnitCostDTO
import io.cloudflight.jems.api.programme.dto.costoption.ProgrammeUnitCostListDTO
import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeUnitCost

fun ProgrammeUnitCost.toDto() = ProgrammeUnitCostDTO(
    id = id,
    name = name,
    description = description,
    type = type,
    costPerUnit = costPerUnit,
    isOneCostCategory = isOneCostCategory,
    categories = categories
)

fun Iterable<ProgrammeUnitCost>.toDto() = map {
    ProgrammeUnitCostListDTO(
        id = it.id,
        name = it.name,
        type = it.type,
        costPerUnit = it.costPerUnit,
        categories = it.categories,
        sortId = determineSortId(it)
    )
} . sortedBy { it.sortId }

fun ProgrammeUnitCostDTO.toModel() = ProgrammeUnitCost(
    id = id,
    name = name,
    description = description,
    type = type,
    costPerUnit = costPerUnit,
    isOneCostCategory = isOneCostCategory?: false,
    categories = categories
)

fun determineSortId(unitCost: ProgrammeUnitCost): Int {
    return if (!unitCost.isOneCostCategory!!) {
        1;
    } else {
        when (unitCost.categories.first()) {
            BudgetCategory.StaffCosts -> 2
            BudgetCategory.TravelAndAccommodationCosts -> 3
            BudgetCategory.ExternalCosts -> 4
            BudgetCategory.EquipmentCosts -> 5
            else -> 6
        }
    }
}
