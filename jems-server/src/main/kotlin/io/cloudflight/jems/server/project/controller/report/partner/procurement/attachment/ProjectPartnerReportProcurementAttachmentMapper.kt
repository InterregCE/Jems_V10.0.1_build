package io.cloudflight.jems.server.project.controller.report.partner.procurement.attachment

import io.cloudflight.jems.api.common.dto.file.JemsFileTypeDTO
import io.cloudflight.jems.api.project.dto.report.partner.procurement.attachment.ProjectReportProcurementFileDTO
import io.cloudflight.jems.server.project.controller.report.partner.partnerReportMapper
import io.cloudflight.jems.server.project.controller.report.partner.sizeToString
import io.cloudflight.jems.server.project.service.report.model.partner.procurement.ProjectReportProcurementFile

fun ProjectReportProcurementFile.toDto() = ProjectReportProcurementFileDTO(
    id = id,
    reportId = reportId,
    createdInThisReport = createdInThisReport,
    name = name,
    type = JemsFileTypeDTO.valueOf(type.name),
    uploaded = uploaded,
    author = partnerReportMapper.map(author),
    size = size,
    sizeString = size.sizeToString(),
    description = description
)
