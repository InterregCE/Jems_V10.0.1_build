package io.cloudflight.jems.server.programme.repository.costoption

import io.cloudflight.jems.api.programme.dto.costoption.BudgetCategory
import io.cloudflight.jems.server.programme.entity.costoption.ProgrammeLumpSumBudgetCategoryEntity
import io.cloudflight.jems.server.programme.entity.costoption.ProgrammeLumpSumEntity
import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeLumpSum

fun ProgrammeLumpSumEntity.toProgrammeUnitCost() = ProgrammeLumpSum(
    id = id,
    name = name,
    description = description,
    cost = cost,
    splittingAllowed = splittingAllowed,
    phase = phase,
    categories = categories.mapTo(HashSet()) { it.category }
)

fun ProgrammeLumpSum.toEntity() = ProgrammeLumpSumEntity(
    id = id ?: 0,
    name = name!!,
    description = description,
    cost = cost!!,
    splittingAllowed = splittingAllowed,
    phase = phase!!,
    categories = if (id == null) mutableSetOf() else categories.toEntity(id)
)

fun Collection<BudgetCategory>.toEntity(programmeLumpSumId: Long) = mapTo(HashSet()) {
    ProgrammeLumpSumBudgetCategoryEntity(programmeLumpSumId = programmeLumpSumId, category = it)
}
