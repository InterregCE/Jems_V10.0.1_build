package io.cloudflight.jems.server.programme.service.translation.model

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import java.time.ZonedDateTime

data class TranslationFileMetaData(
    val language: SystemLanguage,
    val fileType: TranslationFileType,
    val lastModified: ZonedDateTime
)
