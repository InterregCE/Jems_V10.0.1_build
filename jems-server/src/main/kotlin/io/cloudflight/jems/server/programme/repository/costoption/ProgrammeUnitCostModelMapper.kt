package io.cloudflight.jems.server.programme.repository.costoption

import io.cloudflight.jems.api.programme.dto.costoption.BudgetCategory
import io.cloudflight.jems.server.programme.entity.costoption.ProgrammeUnitCostBudgetCategoryEntity
import io.cloudflight.jems.server.programme.entity.costoption.ProgrammeUnitCostEntity
import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeUnitCost

fun ProgrammeUnitCostEntity.toModel() = ProgrammeUnitCost(
    id = id,
    name = name,
    description = description,
    type = type,
    costPerUnit = costPerUnit,
    categories = categories.mapTo(HashSet()) { it.category }
)

fun Iterable<ProgrammeUnitCostEntity>.toModel() = map { it.toModel() }

fun ProgrammeUnitCost.toEntity() = ProgrammeUnitCostEntity(
    id = id ?: 0,
    name = name!!,
    description = description,
    type = type!!,
    costPerUnit = costPerUnit!!,
    categories = if (id == null) mutableSetOf() else categories.toBudgetCategoryEntity(id)
)

fun Collection<BudgetCategory>.toBudgetCategoryEntity(programmeUnitCostId: Long) = mapTo(HashSet()) {
    ProgrammeUnitCostBudgetCategoryEntity(programmeUnitCostId = programmeUnitCostId, category = it)
}
