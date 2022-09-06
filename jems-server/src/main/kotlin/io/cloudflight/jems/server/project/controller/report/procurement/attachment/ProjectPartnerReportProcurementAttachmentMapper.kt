package io.cloudflight.jems.server.project.controller.report.procurement.attachment

import io.cloudflight.jems.api.project.dto.report.file.ProjectPartnerReportFileTypeDTO
import io.cloudflight.jems.api.project.dto.report.partner.procurement.attachment.ProjectReportProcurementFileDTO
import io.cloudflight.jems.server.project.controller.report.partnerReportMapper
import io.cloudflight.jems.server.project.controller.report.sizeToString
import io.cloudflight.jems.server.project.service.report.model.file.procurement.ProjectReportProcurementFile

fun ProjectReportProcurementFile.toDto() = ProjectReportProcurementFileDTO(
    id = id,
    reportId = reportId,
    createdInThisReport = createdInThisReport,
    name = name,
    type = ProjectPartnerReportFileTypeDTO.valueOf(type.name),
    uploaded = uploaded,
    author = partnerReportMapper.map(author),
    size = size,
    sizeString = size.sizeToString(),
    description = description,
)
