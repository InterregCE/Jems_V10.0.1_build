package io.cloudflight.jems.server.project.service.unitcost.model

import io.cloudflight.jems.api.project.dto.InputTranslation

data class ProjectUnitCost(
    val costId: Long,
    val name: Set<InputTranslation> = emptySet(),
    val description: Set<InputTranslation> = emptySet(),
    val unitType: Set<InputTranslation> = emptySet(),
    val pricePerUnit: Long? = 0,
    val numberOfUnits: Long? = 0,
    val total: Long? = 0
)
