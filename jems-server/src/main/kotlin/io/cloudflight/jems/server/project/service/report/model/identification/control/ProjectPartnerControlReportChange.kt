package io.cloudflight.jems.server.project.service.report.model.identification.control

data class ProjectPartnerControlReportChange(
    val controllerFormats: Set<ReportFileFormat>,
    val type: ReportType,
)
