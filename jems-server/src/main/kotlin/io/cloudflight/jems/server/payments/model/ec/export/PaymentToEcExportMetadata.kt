package io.cloudflight.jems.server.payments.model.ec.export

import io.cloudflight.jems.server.payments.model.ec.AccountingYear
import io.cloudflight.jems.server.programme.service.exportProgrammeData.EXPORT_TIMEOUT_IN_MINUTES
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

data class PaymentToEcExportMetadata(
    val id: Long,
    val pluginKey: String,
    val accountingYear: AccountingYear?,
    val fund: ProgrammeFund?,
    val fileName: String?,
    val contentType: String?,

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
        requestTime.until(exportEndedAt ?: ZonedDateTime.now(), ChronoUnit.SECONDS)
}
