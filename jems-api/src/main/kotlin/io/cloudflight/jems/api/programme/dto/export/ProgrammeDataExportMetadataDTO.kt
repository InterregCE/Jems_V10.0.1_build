package io.cloudflight.jems.api.programme.dto.export

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import java.time.ZonedDateTime

data class ProgrammeDataExportMetadataDTO(
    val pluginKey: String,
    val fileName: String?,
    var exportLanguage: SystemLanguage,
    var inputLanguage: SystemLanguage,
    var exportStartedAt: ZonedDateTime? = null,
    var exportEndedAt: ZonedDateTime? = null,
)
