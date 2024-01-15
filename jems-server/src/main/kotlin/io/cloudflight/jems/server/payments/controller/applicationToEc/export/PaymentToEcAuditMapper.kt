package io.cloudflight.jems.server.payments.controller.applicationToEc.export

import io.cloudflight.jems.api.payments.dto.export.PaymentToEcExportMetadataDTO
import io.cloudflight.jems.server.payments.accountingYears.service.toDto
import io.cloudflight.jems.server.payments.model.ec.export.PaymentToEcExportMetadata
import io.cloudflight.jems.server.programme.controller.fund.toDto

fun PaymentToEcExportMetadata.toDto() = PaymentToEcExportMetadataDTO(
    id = id,
    pluginKey = pluginKey,
    fileName = fileName,
    contentType = contentType,
    accountingYear = accountingYear?.toDto(),
    fund = fund?.toDto(),
    requestTime = requestTime,
    exportationTimeInSeconds = getExportationTimeInSeconds(),
    timedOut = isTimedOut(),
    failed = isFailed(),
    readyToDownload = isReadyToDownload()
)
