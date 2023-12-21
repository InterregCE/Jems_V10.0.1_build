package io.cloudflight.jems.server.payments.model.ec.export

import io.cloudflight.jems.server.common.model.AsyncDataExportMetadata
import io.cloudflight.jems.server.payments.model.ec.AccountingYear
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import java.time.ZonedDateTime

data class PaymentToEcExportMetadata(
    val id: Long,
    val accountingYear: AccountingYear?,
    val fund: ProgrammeFund?,

    override val pluginKey: String,
    override val fileName: String?,
    override val contentType: String?,

    override var requestTime: ZonedDateTime,
    override var exportStartedAt: ZonedDateTime?,
    override var exportEndedAt: ZonedDateTime?,
): AsyncDataExportMetadata(
    pluginKey, fileName, contentType, requestTime, exportStartedAt, exportEndedAt
)
