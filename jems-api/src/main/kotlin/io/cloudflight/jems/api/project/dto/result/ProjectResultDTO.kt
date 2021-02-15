package io.cloudflight.jems.api.project.dto.result

import io.cloudflight.jems.api.project.dto.InputTranslation

data class ProjectResultDTO(
    val resultNumber: Int,
    val programmeResultIndicatorId: Long? = null,
    val programmeResultIndicatorIdentifier: String? = null,
    val targetValue: String? = null,
    val periodNumber: Int? = null,
    val description: Set<InputTranslation> = emptySet()
)
