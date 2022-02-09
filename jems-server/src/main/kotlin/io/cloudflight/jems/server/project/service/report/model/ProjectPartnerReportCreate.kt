package io.cloudflight.jems.server.project.service.report.model

data class ProjectPartnerReportCreate(
    val partnerId: Long,
    val reportNumber: Int,
    val status: ReportStatus,
    val version: String,

    val identification: PartnerReportIdentificationCreate,
)
