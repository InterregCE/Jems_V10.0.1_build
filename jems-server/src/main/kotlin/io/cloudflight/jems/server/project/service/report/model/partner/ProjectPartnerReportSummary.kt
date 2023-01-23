package io.cloudflight.jems.server.project.service.report.model.partner

import io.cloudflight.jems.server.project.service.report.model.partner.identification.ProjectPartnerReportPeriod
import java.math.BigDecimal
import java.time.LocalDate
import java.time.ZonedDateTime

data class ProjectPartnerReportSummary(
    val id: Long,
    val reportNumber: Int,
    val status: ReportStatus,
    val version: String,
    val firstSubmission: ZonedDateTime?,
    val controlEnd: ZonedDateTime?,
    val createdAt: ZonedDateTime,
    val startDate: LocalDate?,
    val endDate: LocalDate?,
    val periodDetail: ProjectPartnerReportPeriod?,

    // if certificate linked to project report
    val projectReportId: Long?,
    val projectReportNumber: Int?,

    var totalEligibleAfterControl: BigDecimal?,
    var deletable: Boolean,
)
