package io.cloudflight.jems.server.project.repository.report.partner.model

import io.cloudflight.jems.server.project.service.report.model.partner.ReportStatus
import io.cloudflight.jems.server.project.service.report.model.partner.identification.ProjectPartnerReportPeriod
import java.math.BigDecimal
import java.time.LocalDate
import java.time.ZonedDateTime

data class ReportSummary(
    val id: Long,
    val number: Int,
    val status: ReportStatus,
    val version: String,
    val firstSubmission: ZonedDateTime?,
    val controlEnd: ZonedDateTime?,
    val createdAt: ZonedDateTime,
    val totalEligibleAfterControl: BigDecimal?,
    val periodNumber: Int?,
    val startDate: LocalDate?,
    val endDate: LocalDate?,
    val periodStart: Int?,
    val periodEnd: Int?,
    val periodBudget: BigDecimal?,
    val periodBudgetCumulative: BigDecimal?,
): java.io.Serializable {
    fun getPeriodData(): ProjectPartnerReportPeriod? =
        if (periodNumber != null)
            ProjectPartnerReportPeriod(
                number = periodNumber,
                periodBudget = periodBudget!!,
                periodBudgetCumulative = periodBudgetCumulative!!,
                start = periodStart!!,
                end = periodEnd!!,
            )
        else
            null
}
