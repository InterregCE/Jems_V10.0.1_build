package io.cloudflight.jems.api.programme.dto.priority

import io.cloudflight.jems.api.project.dto.InputTranslation

data class OutputProgrammePrioritySimple (
    val code: String,
    val title: Set<InputTranslation> = emptySet()
)
