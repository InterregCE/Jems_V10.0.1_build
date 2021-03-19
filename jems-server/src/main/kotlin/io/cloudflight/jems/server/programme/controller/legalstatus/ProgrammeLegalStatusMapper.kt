package io.cloudflight.jems.server.programme.controller.legalstatus

import io.cloudflight.jems.api.programme.dto.legalstatus.ProgrammeLegalStatusDTO
import io.cloudflight.jems.api.programme.dto.legalstatus.ProgrammeLegalStatusTypeDTO
import io.cloudflight.jems.server.programme.service.legalstatus.model.ProgrammeLegalStatus
import io.cloudflight.jems.server.programme.service.legalstatus.model.ProgrammeLegalStatusType

fun Iterable<ProgrammeLegalStatus>.toDto() = map {
    ProgrammeLegalStatusDTO(
        id = it.id,
        description = it.description ,
        type = ProgrammeLegalStatusTypeDTO.valueOf(it.type.name)
    )
}

fun Iterable<ProgrammeLegalStatusDTO>.toModel() = map {
    ProgrammeLegalStatus(
        id = it.id ?: 0,
        description = it.description,
        type = ProgrammeLegalStatusType.valueOf(it.type.name)
    )
}
