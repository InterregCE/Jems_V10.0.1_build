package io.cloudflight.jems.api.project.dto.report.partner

import io.cloudflight.jems.api.project.dto.report.partner.identification.ProjectPartnerReportPeriodDTO
import java.math.BigDecimal
import java.time.LocalDate
import java.time.ZonedDateTime

data class ProjectPartnerReportSummaryDTO(
    val id: Long,
    val reportNumber: Int,
    val status: ReportStatusDTO,
    val linkedFormVersion: String,
    val firstSubmission: ZonedDateTime?,
    val controlEnd: ZonedDateTime?,
    val createdAt: ZonedDateTime,
    val startDate: LocalDate?,
    val endDate: LocalDate?,
    val periodDetail: ProjectPartnerReportPeriodDTO?,
    val totalEligibleAfterControl: BigDecimal?,
    val deletable: Boolean,
)
