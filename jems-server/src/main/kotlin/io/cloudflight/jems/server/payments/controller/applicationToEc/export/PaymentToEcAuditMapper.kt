package io.cloudflight.jems.server.payments.controller.applicationToEc.export

import io.cloudflight.jems.api.payments.dto.export.PaymentToEcExportMetadataDTO
import io.cloudflight.jems.api.programme.dto.fund.ProgrammeFundTypeDTO
import io.cloudflight.jems.server.payments.model.ec.export.PaymentToEcExportMetadata
import io.cloudflight.jems.server.project.controller.report.partner.toDto

fun PaymentToEcExportMetadata.toDto() = PaymentToEcExportMetadataDTO(
    id = id,
    pluginKey,
    generatedFile = generatedFile.toDto(),
    accountingYear,
    fundType = if (fundType !== null) ProgrammeFundTypeDTO.valueOf(fundType.name) else null,
    requestTime,
    exportationTimeInSeconds = getExportationTimeInSeconds(),
    timedOut = isTimedOut(),
    failed = isFailed(),
    readyToDownload = isReadyToDownload()
)
