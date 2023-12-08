package io.cloudflight.jems.api.payments.dto.export

import io.cloudflight.jems.api.common.dto.file.JemsFileDTO
import io.cloudflight.jems.api.programme.dto.fund.ProgrammeFundTypeDTO
import java.time.ZonedDateTime

data class PaymentToEcExportMetadataDTO(
    val id: Long,
    val pluginKey: String,
    val generatedFile: JemsFileDTO,
    val accountingYear: Short?,
    val fundType: ProgrammeFundTypeDTO?,
    var requestTime: ZonedDateTime? = null,
    var exportationTimeInSeconds: Long? = null,
    var timedOut: Boolean,
    var failed: Boolean,
    var readyToDownload: Boolean
)
