package io.cloudflight.jems.server.project.service.report.model.partner

import java.time.ZonedDateTime

data class ProjectPartnerReport(
    val id: Long,
    val reportNumber: Int,
    val status: ReportStatus,
    val version: String,
    val firstSubmission: ZonedDateTime?,
    val lastResubmission: ZonedDateTime?,
    val controlEnd: ZonedDateTime?,

    val lastControlReopening: ZonedDateTime?,
    val projectReportId: Long?,
    val projectReportNumber: Int?,

    val identification: PartnerReportIdentification,
)
