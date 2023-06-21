package io.cloudflight.jems.api.call.dto.translation

import io.cloudflight.jems.api.common.dto.file.JemsFileMetadataDTO
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage

data class CallTranslationFileDTO(
    val language: SystemLanguage,
    val file: JemsFileMetadataDTO?,
    val defaultFromProgramme: String?,
)
