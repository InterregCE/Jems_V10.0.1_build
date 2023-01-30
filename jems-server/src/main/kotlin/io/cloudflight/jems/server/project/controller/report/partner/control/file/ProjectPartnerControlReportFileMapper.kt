package io.cloudflight.jems.server.project.controller.report.partner.control.file

import io.cloudflight.jems.api.project.dto.report.file.PartnerReportControlFileDTO
import io.cloudflight.jems.server.project.controller.report.partner.toDto
import io.cloudflight.jems.server.project.service.report.model.partner.control.file.PartnerReportControlFile

fun PartnerReportControlFile.toDto() = PartnerReportControlFileDTO(
    id = id,
    reportId = reportId,
    generatedFile = generatedFile.toDto(),
    signedFile = signedFile?.toDto(),
)
