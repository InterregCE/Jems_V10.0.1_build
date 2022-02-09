package io.cloudflight.jems.server.project.service.report.model

import java.time.ZonedDateTime

data class ProjectPartnerReportSummary(
    val id: Long,
    val reportNumber: Int,
    val status: ReportStatus,
    val version: String,
    val firstSubmission: ZonedDateTime?,
    val createdAt: ZonedDateTime,
)
