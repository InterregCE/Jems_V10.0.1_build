package io.cloudflight.jems.server.project.service.report.model

import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import java.time.ZonedDateTime

data class ProjectPartnerReportSubmissionSummary(
    val id: Long,
    val reportNumber: Int,
    val status: ReportStatus,
    val version: String,
    val firstSubmission: ZonedDateTime?,
    val createdAt: ZonedDateTime,

    val projectIdentifier: String,
    val projectAcronym: String,
    val partnerNumber: Int,
    val partnerRole: ProjectPartnerRole,
)
