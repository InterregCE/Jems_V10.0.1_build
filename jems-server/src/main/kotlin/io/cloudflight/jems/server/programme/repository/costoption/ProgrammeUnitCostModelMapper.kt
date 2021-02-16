package io.cloudflight.jems.server.programme.repository.costoption

import io.cloudflight.jems.api.programme.dto.costoption.BudgetCategory
import io.cloudflight.jems.server.programme.entity.costoption.ProgrammeUnitCostBudgetCategoryEntity
import io.cloudflight.jems.server.programme.entity.costoption.ProgrammeUnitCostEntity
import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeUnitCost

fun ProgrammeUnitCostEntity.toProgrammeUnitCost() = ProgrammeUnitCost(
    id = id,
    name = name,
    description = description,
    type = type,
    costPerUnit = costPerUnit,
    isOneCostCategory = isOneCostCategory,
    categories = categories.mapTo(HashSet()) { it.category }
)

fun Iterable<ProgrammeUnitCostEntity>.toProgrammeUnitCost() = map { it.toProgrammeUnitCost() }

fun ProgrammeUnitCost.toEntity() = ProgrammeUnitCostEntity(
    id = id ?: 0,
    name = name!!,
    description = description,
    type = type!!,
    costPerUnit = costPerUnit!!,
    isOneCostCategory = isOneCostCategory,
    categories = if (id == null) mutableSetOf() else categories.toBudgetCategoryEntity(id)
)

fun Collection<BudgetCategory>.toBudgetCategoryEntity(programmeUnitCostId: Long) = mapTo(HashSet()) {
    ProgrammeUnitCostBudgetCategoryEntity(programmeUnitCostId = programmeUnitCostId, category = it)
}
