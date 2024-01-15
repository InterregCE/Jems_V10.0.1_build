package io.cloudflight.jems.server.project.service.report.model.project

import java.time.ZonedDateTime

data class ProjectReportSubmissionSummary(
    val id: Long,
    val reportNumber: Int,
    val status: ProjectReportStatus,
    val version: String,
    val firstSubmission: ZonedDateTime?,
    val createdAt: ZonedDateTime,

    val projectId: Long,
    val projectIdentifier: String,
    val projectAcronym: String,
    val periodNumber: Int?
)
