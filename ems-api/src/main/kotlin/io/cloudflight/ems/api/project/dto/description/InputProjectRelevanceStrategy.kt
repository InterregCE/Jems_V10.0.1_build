package io.cloudflight.ems.api.project.dto.description

import io.cloudflight.ems.api.strategy.ProgrammeStrategy

data class InputProjectRelevanceStrategy(
    val strategy: ProgrammeStrategy,
    val specification: String? = null
)
