package io.cloudflight.jems.server.project.controller.unitcost

import io.cloudflight.jems.api.project.dto.budget.ProjectUnitCostDTO
import io.cloudflight.jems.server.project.service.unitcost.model.ProjectUnitCost

fun List<ProjectUnitCost>.toDto() = map {
    ProjectUnitCostDTO(
        costId = it.costId,
        name = it.name,
        description = it.description,
        unitType = it.unitType,
        pricePerUnit = it.pricePerUnit,
        numberOfUnits = it.numberOfUnits,
        total = it.total
    )
}