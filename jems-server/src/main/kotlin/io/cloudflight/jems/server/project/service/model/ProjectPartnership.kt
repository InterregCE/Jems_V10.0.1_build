package io.cloudflight.jems.server.project.service.model

import io.cloudflight.jems.api.project.dto.InputTranslation

data class ProjectPartnership(
    val partnership: Set<InputTranslation> = emptySet()
)
