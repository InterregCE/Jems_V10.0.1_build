package io.cloudflight.jems.server.project.service.result.model

import io.cloudflight.jems.api.project.dto.InputTranslation
import java.math.BigDecimal

data class ProjectResult(
    val resultNumber: Int = 0, // this is computed automatically in persistence
    val programmeResultIndicatorId: Long? = null,
    val programmeResultIndicatorIdentifier: String? = null,
    val baseline: BigDecimal,
    val targetValue: BigDecimal? = null,
    val periodNumber: Int? = null,
    val description: Set<InputTranslation> = emptySet()
)
