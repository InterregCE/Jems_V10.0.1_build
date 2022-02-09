package io.cloudflight.jems.server.programme.controller.legalstatus

import io.cloudflight.jems.api.programme.dto.legalstatus.ProgrammeLegalStatusDTO
import io.cloudflight.jems.api.programme.dto.legalstatus.ProgrammeLegalStatusTypeDTO
import io.cloudflight.jems.server.programme.service.legalstatus.model.ProgrammeLegalStatus
import io.cloudflight.jems.server.programme.service.legalstatus.model.ProgrammeLegalStatusType

fun Iterable<ProgrammeLegalStatus>.toDto() = map { it.toDto() }

fun ProgrammeLegalStatus.toDto() = ProgrammeLegalStatusDTO(
    id = id,
    description = description,
    type = ProgrammeLegalStatusTypeDTO.valueOf(type.name)
)

fun Iterable<ProgrammeLegalStatusDTO>.toModel() = map {
    ProgrammeLegalStatus(
        id = it.id ?: 0,
        description = it.description,
        type = ProgrammeLegalStatusType.valueOf(it.type.name)
    )
}
