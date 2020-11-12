package io.cloudflight.jems.server.programme.service

import io.cloudflight.jems.api.programme.dto.InputProgrammeLegalStatus
import io.cloudflight.jems.api.programme.dto.OutputProgrammeLegalStatus
import io.cloudflight.jems.server.programme.entity.ProgrammeLegalStatus

fun InputProgrammeLegalStatus.toEntity() = ProgrammeLegalStatus(
    id = id ?: 0,
    description = description
)

fun OutputProgrammeLegalStatus.toEntity() = ProgrammeLegalStatus(
    id = id,
    description = description
)

fun ProgrammeLegalStatus.toOutputProgrammeLegalStatus(): OutputProgrammeLegalStatus {
    return if (id <= 2) { // only first 2 statuses are translated
        OutputProgrammeLegalStatus(id = id)
    } else {
        OutputProgrammeLegalStatus(id = id, description = description)
    }
}
