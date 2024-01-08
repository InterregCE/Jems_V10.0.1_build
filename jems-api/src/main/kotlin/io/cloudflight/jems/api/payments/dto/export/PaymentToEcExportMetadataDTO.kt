package io.cloudflight.jems.api.payments.dto.export

import io.cloudflight.jems.api.payments.dto.applicationToEc.AccountingYearDTO
import io.cloudflight.jems.api.programme.dto.fund.ProgrammeFundDTO
import java.time.ZonedDateTime

data class PaymentToEcExportMetadataDTO(
    val id: Long,
    val pluginKey: String,
    val fileName: String?,
    val contentType: String?,
    val accountingYear: AccountingYearDTO?,
    val fund: ProgrammeFundDTO?,
    var requestTime: ZonedDateTime? = null,
    var exportationTimeInSeconds: Long? = null,
    var timedOut: Boolean,
    var failed: Boolean,
    var readyToDownload: Boolean
)
