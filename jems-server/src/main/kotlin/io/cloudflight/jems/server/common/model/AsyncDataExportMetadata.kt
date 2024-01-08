package io.cloudflight.jems.server.common.model

import io.cloudflight.jems.server.programme.service.exportProgrammeData.EXPORT_TIMEOUT_IN_MINUTES
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

open class AsyncDataExportMetadata (
    open val pluginKey: String,
    open val fileName: String?,
    open val contentType: String?,

    open var requestTime: ZonedDateTime,
    open var exportStartedAt: ZonedDateTime?,
    open var exportEndedAt: ZonedDateTime?,
) {
    fun isTimedOut() =
        exportEndedAt == null && requestTime.isBefore(ZonedDateTime.now().minusMinutes(EXPORT_TIMEOUT_IN_MINUTES))

    fun isFailed() =
        exportEndedAt != null && fileName == null && contentType == null

    fun isReadyToDownload() =
        exportEndedAt != null && fileName != null && contentType != null

    fun getExportationTimeInSeconds() =
        requestTime.until(exportEndedAt ?: ZonedDateTime.now(), ChronoUnit.SECONDS)
}
