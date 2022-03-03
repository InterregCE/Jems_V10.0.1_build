package io.cloudflight.jems.server.programme.service.model

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.server.programme.service.exportProgrammeData.EXPORT_TIMEOUT_IN_MINUTES
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

data class ProgrammeDataExportMetadata(
    val pluginKey: String,
    val fileName: String?,
    val contentType: String?,
    var exportLanguage: SystemLanguage,
    var inputLanguage: SystemLanguage,
    var requestTime: ZonedDateTime,
    var exportStartedAt: ZonedDateTime?,
    var exportEndedAt: ZonedDateTime?,
) {
    fun isTimedOut() =
        exportEndedAt == null && requestTime.isBefore(ZonedDateTime.now().minusMinutes(EXPORT_TIMEOUT_IN_MINUTES))

    fun isFailed() =
        exportEndedAt != null && fileName == null && contentType == null

    fun isReadyToDownload() =
        exportEndedAt != null && fileName != null && contentType != null

    fun getExportationTimeInSeconds() =
        exportStartedAt?.until(exportEndedAt, ChronoUnit.SECONDS)
}
