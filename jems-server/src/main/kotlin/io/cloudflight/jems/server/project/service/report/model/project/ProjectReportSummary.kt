package io.cloudflight.jems.server.project.service.report.model.project

import io.cloudflight.jems.server.project.service.contracting.model.reporting.ContractingDeadlineType
import io.cloudflight.jems.server.project.service.model.ProjectPeriod
import java.math.BigDecimal
import java.time.LocalDate
import java.time.ZonedDateTime

data class ProjectReportSummary(
    val id: Long,
    val reportNumber: Int,
    val status: ProjectReportStatus,
    val linkedFormVersion: String,
    val startDate: LocalDate?,
    val endDate: LocalDate?,
    val type: ContractingDeadlineType?,
    val periodDetail: ProjectPeriod?,
    val reportingDate: LocalDate?,
    val createdAt: ZonedDateTime,
    val firstSubmission: ZonedDateTime?,
    val verificationDate: ZonedDateTime?,
    val verificationEndDate: ZonedDateTime?,
    var deletable: Boolean,
    val amountRequested: BigDecimal?,
    val totalEligibleAfterVerification: BigDecimal?
)
