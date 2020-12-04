package io.cloudflight.jems.api.project.dto.description

import io.cloudflight.jems.api.programme.dto.strategy.ProgrammeStrategy
import io.cloudflight.jems.api.project.dto.InputTranslation

data class InputProjectRelevanceStrategy(
    val strategy: ProgrammeStrategy? = null,
    val specification: Set<InputTranslation> = emptySet()
)
