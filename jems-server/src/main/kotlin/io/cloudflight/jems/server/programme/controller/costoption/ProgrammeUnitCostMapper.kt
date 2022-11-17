package io.cloudflight.jems.server.programme.controller.costoption

import io.cloudflight.jems.api.programme.dto.costoption.ProgrammeUnitCostDTO
import io.cloudflight.jems.api.programme.dto.costoption.ProgrammeUnitCostListDTO
import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeUnitCost

fun ProgrammeUnitCost.toDto() = ProgrammeUnitCostDTO(
    id = id,
    projectDefined = projectId != null,
    name = name,
    description = description,
    type = type,
    justification = justification,
    costPerUnit = costPerUnit,
    costPerUnitForeignCurrency = costPerUnitForeignCurrency,
    foreignCurrencyCode = foreignCurrencyCode,
    oneCostCategory = isOneCostCategory,
    categories = categories
)

fun Iterable<ProgrammeUnitCost>.toDto() = sorted().map {
    ProgrammeUnitCostListDTO(
        id = it.id,
        name = it.name,
        type = it.type,
        costPerUnit = it.costPerUnit,
        categories = it.categories
    )
}

fun Iterable<ProgrammeUnitCost>.toDetailDto() = sorted().map { it.toDto() }

fun ProgrammeUnitCostDTO.toModel() = ProgrammeUnitCost(
    id = id ?: 0,
    projectId = null,
    name = name,
    description = description,
    type = type,
    justification = justification,
    costPerUnit = costPerUnit,
    costPerUnitForeignCurrency = costPerUnitForeignCurrency,
    foreignCurrencyCode = foreignCurrencyCode,
    isOneCostCategory = oneCostCategory,
    categories = categories
)
