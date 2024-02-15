package io.cloudflight.jems.api.project.dto.report.project

import io.cloudflight.jems.api.project.dto.ProjectPeriodDTO
import io.cloudflight.jems.api.project.dto.contracting.reporting.ContractingDeadlineTypeDTO
import java.time.LocalDate
import java.time.ZonedDateTime

data class ProjectReportDTO(
    val id: Long,
    val reportNumber: Int,
    val status: ProjectReportStatusDTO,
    val linkedFormVersion: String,

    val startDate: LocalDate?,
    val endDate: LocalDate?,

    val deadlineId: Long?,
    val type: ContractingDeadlineTypeDTO?,
    val periodDetail: ProjectPeriodDTO?,
    val reportingDate: LocalDate?,
    val finalReport: Boolean?,

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

    val paymentIdsInstallmentExists: Set<Long>,
    val paymentToEcIdsReportIncluded: Set<Long>,
)
