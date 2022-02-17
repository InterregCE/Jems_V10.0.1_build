package io.cloudflight.jems.server.programme.service.model

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import java.time.ZonedDateTime

data class ProgrammeDataExportMetadata(
    val pluginKey: String,
    val fileName: String?,
    val contentType: String?,
    var exportLanguage: SystemLanguage,
    var inputLanguage: SystemLanguage,
    var requestTime: ZonedDateTime,
    var exportStartedAt: ZonedDateTime?,
    var exportEndedAt: ZonedDateTime?,
)
