package io.cloudflight.jems.server.call.service.model.translation

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.server.common.file.service.model.JemsFileMetadata

data class CallTranslationFile(
    val language: SystemLanguage,
    val file: JemsFileMetadata?,
    val defaultFromProgramme: String?,
)
