package io.cloudflight.ems.programme.service

import io.cloudflight.ems.api.programme.dto.InputProgrammeLegalStatus
import io.cloudflight.ems.api.programme.dto.OutputProgrammeLegalStatus
import io.cloudflight.ems.programme.entity.ProgrammeLegalStatus

fun InputProgrammeLegalStatus.toEntity() = ProgrammeLegalStatus(
    id = id,
    description = if (id == null) description else null
)

fun OutputProgrammeLegalStatus.toEntity() = ProgrammeLegalStatus(
    id = id,
    description = description
)

fun ProgrammeLegalStatus.toOutputProgrammeLegalStatus(): OutputProgrammeLegalStatus {
    return if (id!! <= 2) { // only first 2 statuses are translated
        OutputProgrammeLegalStatus(id = id!!)
    } else {
        OutputProgrammeLegalStatus(id = id!!, description = description)
    }
}
