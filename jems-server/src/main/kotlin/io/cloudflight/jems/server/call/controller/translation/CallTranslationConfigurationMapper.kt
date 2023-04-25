package io.cloudflight.jems.server.call.controller.translation

import io.cloudflight.jems.api.call.dto.translation.CallTranslationFileDTO
import io.cloudflight.jems.server.call.service.model.translation.CallTranslationFile
import io.cloudflight.jems.server.project.controller.report.partner.toDto

fun CallTranslationFile.toDto() = CallTranslationFileDTO(
    language = language,
    file = file?.toDto(),
    defaultFromProgramme = defaultFromProgramme,
)

fun List<CallTranslationFile>.toDto() = map { it.toDto() }
