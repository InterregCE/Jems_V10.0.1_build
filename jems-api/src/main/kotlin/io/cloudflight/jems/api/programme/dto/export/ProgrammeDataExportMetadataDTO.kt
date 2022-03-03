package io.cloudflight.jems.api.programme.dto.export

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import java.time.ZonedDateTime

data class ProgrammeDataExportMetadataDTO(
    val pluginKey: String,
    val fileName: String?,
    var exportLanguage: SystemLanguage,
    var inputLanguage: SystemLanguage,
    var requestTime: ZonedDateTime? = null,
    var exportationTimeInSeconds: Long? = null,
    var timedOut: Boolean,
    var failed: Boolean,
    var readyToDownload: Boolean
)
