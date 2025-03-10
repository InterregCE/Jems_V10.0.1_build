package io.cloudflight.jems.server.project.service.report.model.partner

import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.report.model.partner.identification.ProjectPartnerReportPeriod
import java.math.BigDecimal
import java.time.LocalDate
import java.time.ZonedDateTime

data class ProjectPartnerReportSummary(
    val id: Long,
    val projectId: Long,
    val partnerId: Long,
    val projectCustomIdentifier: String,
    val partnerRole: ProjectPartnerRole,
    val partnerNumber: Int,
    val partnerAbbreviation: String,
    val reportNumber: Int,
    val status: ReportStatus,
    val version: String,
    val firstSubmission: ZonedDateTime?,
    val lastReSubmission: ZonedDateTime?,
    var controlEnd: ZonedDateTime?,
    val createdAt: ZonedDateTime,
    val startDate: LocalDate?,
    val endDate: LocalDate?,
    val periodDetail: ProjectPartnerReportPeriod?,

    // if certificate linked to project report
    val projectReportId: Long?,
    val projectReportNumber: Int?,

    var totalEligibleAfterControl: BigDecimal?,
    var totalAfterSubmitted: BigDecimal?,
    var deletable: Boolean,
)
