package io.cloudflight.jems.api.project.dto.report.partner.identification.control

data class ProjectPartnerControlReportChangeDTO(
    val controllerFormats: Set<ReportFileFormatDTO>,
    val type: ReportTypeDTO,
)
