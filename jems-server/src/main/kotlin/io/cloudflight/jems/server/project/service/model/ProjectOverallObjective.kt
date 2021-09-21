package io.cloudflight.jems.server.project.service.model

import io.cloudflight.jems.api.project.dto.InputTranslation

data class ProjectOverallObjective(
    val overallObjective: Set<InputTranslation> = emptySet()
)
