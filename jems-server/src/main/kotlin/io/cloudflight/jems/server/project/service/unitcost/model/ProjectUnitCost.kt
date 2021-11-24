package io.cloudflight.jems.server.project.service.unitcost.model

import io.cloudflight.jems.api.project.dto.InputTranslation
import java.math.BigDecimal

data class ProjectUnitCost(
    val costId: Long,
    val name: Set<InputTranslation> = emptySet(),
    val description: Set<InputTranslation> = emptySet(),
    val unitType: Set<InputTranslation> = emptySet(),
    val pricePerUnit: BigDecimal? = null,
    val numberOfUnits: BigDecimal? = null,
    val total: BigDecimal? = null
)
