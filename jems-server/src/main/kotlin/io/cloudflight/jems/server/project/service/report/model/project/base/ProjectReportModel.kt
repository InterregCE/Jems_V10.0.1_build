package io.cloudflight.jems.server.project.service.report.model.project.base

import io.cloudflight.jems.server.project.service.contracting.model.reporting.ContractingDeadlineType
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportStatus
import java.math.BigDecimal
import java.time.LocalDate
import java.time.ZonedDateTime

data class ProjectReportModel(
    val id: Long = 0L,
    val reportNumber: Int,
    val status: ProjectReportStatus,
    val linkedFormVersion: String,

    val startDate: LocalDate?,
    val endDate: LocalDate?,

    val deadlineId: Long?,
    val type: ContractingDeadlineType?,
    val periodNumber: Int?,
    val reportingDate: LocalDate?,

    val projectId: Long,
    val projectIdentifier: String,
    val projectAcronym: String,
    val leadPartnerNameInOriginalLanguage: String,
    val leadPartnerNameInEnglish: String,

    val createdAt: ZonedDateTime,
    val firstSubmission: ZonedDateTime?,
    val verificationDate: ZonedDateTime?,

    val verificationEndDate: ZonedDateTime?,
    val amountRequested: BigDecimal?,
    val totalEligibleAfterVerification: BigDecimal?
)
