package io.cloudflight.jems.server.programme.service.legalstatus.model

import io.cloudflight.jems.api.project.dto.InputTranslation

data class ProgrammeLegalStatus(
    val id: Long = 0,
    val type: ProgrammeLegalStatusType = ProgrammeLegalStatusType.OTHER,
    val description: Set<InputTranslation> = emptySet()
)
