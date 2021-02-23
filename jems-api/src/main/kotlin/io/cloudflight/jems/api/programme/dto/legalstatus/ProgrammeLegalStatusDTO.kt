package io.cloudflight.jems.api.programme.dto.legalstatus

import io.cloudflight.jems.api.project.dto.InputTranslation

data class ProgrammeLegalStatusDTO (
    val id: Long? = null,
    val description: Set<InputTranslation> = emptySet()
)
