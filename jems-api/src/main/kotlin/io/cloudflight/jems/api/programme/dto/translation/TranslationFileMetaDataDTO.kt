package io.cloudflight.jems.api.programme.dto.translation

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import java.time.ZonedDateTime

data class TranslationFileMetaDataDTO(
    val language: SystemLanguage,
    val fileType: TranslationFileTypeDTO,
    val lastModified: ZonedDateTime
)
