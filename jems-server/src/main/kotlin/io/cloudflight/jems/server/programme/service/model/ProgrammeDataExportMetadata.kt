package io.cloudflight.jems.server.programme.service.model

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.server.common.model.AsyncDataExportMetadata
import java.time.ZonedDateTime

data class ProgrammeDataExportMetadata(
    var exportLanguage: SystemLanguage,
    var inputLanguage: SystemLanguage,

    override val pluginKey: String,
    override val fileName: String?,
    override val contentType: String?,

    override var requestTime: ZonedDateTime,
    override var exportStartedAt: ZonedDateTime?,
    override var exportEndedAt: ZonedDateTime?,
) : AsyncDataExportMetadata(
    pluginKey, fileName, contentType, requestTime, exportStartedAt, exportEndedAt
)
