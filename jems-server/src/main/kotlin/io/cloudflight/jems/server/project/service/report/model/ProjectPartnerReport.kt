package io.cloudflight.jems.server.project.service.report.model

data class ProjectPartnerReport(
    val id: Long,
    val reportNumber: Int,
    val status: ReportStatus,
    val version: String,

    val identification: PartnerReportIdentification,
)
