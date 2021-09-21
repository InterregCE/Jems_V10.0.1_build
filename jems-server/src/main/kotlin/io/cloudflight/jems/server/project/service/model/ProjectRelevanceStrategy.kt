package io.cloudflight.jems.server.project.service.model

import io.cloudflight.jems.api.programme.dto.strategy.ProgrammeStrategy
import io.cloudflight.jems.api.project.dto.InputTranslation

data class ProjectRelevanceStrategy(
    val strategy: ProgrammeStrategy? = null,
    val specification: Set<InputTranslation> = emptySet()
)
