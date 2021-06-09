package io.cloudflight.jems.server.project.service.model

import io.cloudflight.jems.api.project.dto.InputTranslation

data class ProjectRelevanceSynergy(
    val synergy: Set<InputTranslation> = emptySet(),
    val specification: Set<InputTranslation> = emptySet()
)
