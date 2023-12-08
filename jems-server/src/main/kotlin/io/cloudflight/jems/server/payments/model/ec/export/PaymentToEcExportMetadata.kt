package io.cloudflight.jems.server.payments.model.ec.export

import io.cloudflight.jems.server.common.file.service.model.JemsFile
import io.cloudflight.jems.server.programme.service.exportProgrammeData.EXPORT_TIMEOUT_IN_MINUTES
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFundType
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

data class PaymentToEcExportMetadata(
    val id: Long,
    val pluginKey: String,
    val generatedFile: JemsFile,
    val accountingYear: Short?,
    val fundType: ProgrammeFundType?,
    var requestTime: ZonedDateTime,
    var exportStartedAt: ZonedDateTime?,
    var exportEndedAt: ZonedDateTime?,
) {
    fun isTimedOut() =
        exportEndedAt == null && requestTime.isBefore(ZonedDateTime.now().minusMinutes(EXPORT_TIMEOUT_IN_MINUTES))

    fun isFailed() =
        exportEndedAt != null

    fun isReadyToDownload() =
        exportEndedAt != null

    fun getExportationTimeInSeconds() =
        requestTime.until(exportEndedAt ?: ZonedDateTime.now(), ChronoUnit.SECONDS)
}
