package io.cloudflight.jems.api.project.dto.budget

import io.cloudflight.jems.api.project.dto.InputTranslation

data class ProjectUnitCostDTO(
    val costId: Long,
    val name: Set<InputTranslation> = emptySet(),
    val description: Set<InputTranslation> = emptySet(),
    val unitType: Set<InputTranslation> = emptySet(),
    val pricePerUnit: Long?,
    val numberOfUnits: Long?,
    val total: Long?
)
