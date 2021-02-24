package io.cloudflight.jems.server.programme.controller.legalstatus

import io.cloudflight.jems.api.programme.dto.legalstatus.ProgrammeLegalStatusDTO
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.programme.service.legalstatus.model.ProgrammeLegalStatus
import io.cloudflight.jems.server.programme.service.legalstatus.model.ProgrammeLegalStatusTranslatedValue
import io.cloudflight.jems.server.project.controller.workpackage.extractField

fun Iterable<ProgrammeLegalStatus>.toDto() = map {
    ProgrammeLegalStatusDTO(
        id = it.id,
        description = it.translatedValues.extractField { it.description },
    )
}

fun Iterable<ProgrammeLegalStatusDTO>.toModel() = map {
    ProgrammeLegalStatus(
        id = it.id ?: 0,
        translatedValues = it.description.toModel(),
    )
}

private fun Set<InputTranslation>.toModel() =
    map { ProgrammeLegalStatusTranslatedValue(language = it.language, description = it.translation) }
        .filter { !it.isEmpty() }
        .toSet()
