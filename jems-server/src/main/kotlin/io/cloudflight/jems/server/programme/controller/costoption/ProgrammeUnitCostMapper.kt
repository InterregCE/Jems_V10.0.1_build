package io.cloudflight.jems.server.programme.controller.costoption

import io.cloudflight.jems.api.programme.dto.costoption.ProgrammeUnitCostDTO
import io.cloudflight.jems.api.programme.dto.costoption.ProgrammeUnitCostListDTO
import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeUnitCost

fun ProgrammeUnitCost.toDto() = ProgrammeUnitCostDTO(
    id = id,
    name = name,
    description = description,
    type = type,
    costPerUnit = costPerUnit,
    oneCostCategory = isOneCostCategory,
    categories = categories
)

fun Iterable<ProgrammeUnitCost>.toDto() = sorted().map {
    ProgrammeUnitCostListDTO(
        id = it.id,
        name = it.name,
        type = it.type,
        costPerUnit = it.costPerUnit,
        categories = it.categories,
    )
}

fun ProgrammeUnitCostDTO.toModel() = ProgrammeUnitCost(
    id = id ?: 0,
    name = name,
    description = description,
    type = type,
    costPerUnit = costPerUnit,
    isOneCostCategory = oneCostCategory,
    categories = categories
)
