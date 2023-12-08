package io.cloudflight.jems.server.project.service.report.model.project

import io.cloudflight.jems.server.project.service.contracting.model.reporting.ContractingDeadlineType
import io.cloudflight.jems.server.project.service.model.ProjectPeriod
import java.time.LocalDate
import java.time.ZonedDateTime

data class ProjectReport(
    val id: Long,
    val reportNumber: Int,
    val status: ProjectReportStatus,
    val linkedFormVersion: String,

    val startDate: LocalDate?,
    val endDate: LocalDate?,

    val deadlineId: Long?,
    val type: ContractingDeadlineType?,
    val periodDetail: ProjectPeriod?,
    val reportingDate: LocalDate?,

    val projectId: Long,
    val projectIdentifier: String,
    val projectAcronym: String,
    val leadPartnerNameInOriginalLanguage: String,
    val leadPartnerNameInEnglish: String,

    val createdAt: ZonedDateTime,
    val firstSubmission: ZonedDateTime?,
    val verificationDate: LocalDate?,
    val verificationEndDate: ZonedDateTime?,
    val verificationLastReOpenDate: ZonedDateTime?,

    val paymentIdsInstallmentExists: Set<Long> = setOf(),
    val paymentToEcIdsReportIncluded: Set<Long> = setOf(),
)
