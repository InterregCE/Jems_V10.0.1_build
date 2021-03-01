package io.cloudflight.jems.server.programme.service.indicator.model

import io.cloudflight.jems.api.project.dto.InputTranslation

data class OutputIndicatorSummary(
    val id: Long?,
    val identifier: String,
    val code: String?,
    val name: Set<InputTranslation> = emptySet(),
    val programmePriorityCode: String?,
    val measurementUnit: Set<InputTranslation> = emptySet(),
)
