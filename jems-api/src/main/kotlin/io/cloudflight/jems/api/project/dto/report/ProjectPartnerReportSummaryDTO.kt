package io.cloudflight.jems.api.project.dto.report

import java.time.ZonedDateTime

data class ProjectPartnerReportSummaryDTO(
    val id: Long,
    val reportNumber: Int,
    val status: ReportStatusDTO,
    val linkedFormVersion: String,
    val firstSubmission: ZonedDateTime?,
    val createdAt: ZonedDateTime,
)
