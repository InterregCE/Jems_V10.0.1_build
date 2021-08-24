package io.cloudflight.jems.api.project.dto.result

import io.cloudflight.jems.api.project.dto.InputTranslation
import java.math.BigDecimal

data class ProjectResultDTO(
    val resultNumber: Int = 0,
    val programmeResultIndicatorId: Long? = null,
    val programmeResultIndicatorIdentifier: String? = null,
    val baseline: BigDecimal?,
    val targetValue: BigDecimal? = null,
    val periodNumber: Int? = null,
    val description: Set<InputTranslation> = emptySet()
)
