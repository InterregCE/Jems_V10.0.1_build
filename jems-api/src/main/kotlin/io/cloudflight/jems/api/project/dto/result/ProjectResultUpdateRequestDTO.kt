package io.cloudflight.jems.api.project.dto.result

import io.cloudflight.jems.api.project.dto.InputTranslation
import java.math.BigDecimal

data class ProjectResultUpdateRequestDTO(
    val programmeResultIndicatorId: Long? = null,
    val baseline: BigDecimal,
    val targetValue: BigDecimal? = null,
    val periodNumber: Int? = null,
    val description: Set<InputTranslation> = emptySet(),
    val deactivated: Boolean,
    val resultNumber: Int
)
