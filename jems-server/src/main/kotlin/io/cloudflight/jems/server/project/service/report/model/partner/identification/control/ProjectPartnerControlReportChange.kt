package io.cloudflight.jems.server.project.service.report.model.partner.identification.control

data class ProjectPartnerControlReportChange(
    val controllerFormats: Set<ReportFileFormat>,
    val type: ReportType,
    val designatedController: ReportDesignatedController,
    val reportVerification: ReportVerification
)
