package io.cloudflight.jems.server.project.repository.report.partner.model

import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.report.model.partner.identification.ProjectPartnerReportPeriod
import java.math.BigDecimal
import java.time.LocalDate

data class ReportIdentificationSummary(
    val partnerReportId: Long,
    val partnerReportNumber: Int,
    val totalEligibleAfterControl: BigDecimal?,

    val partnerId: Long,
    val partnerNumber: Int,
    val partnerRole: ProjectPartnerRole,

    val startDate: LocalDate?,
    val endDate: LocalDate?,
    val periodNumber: Int?,
    val periodStart: Int?,
    val periodEnd: Int?,
    val periodBudget: BigDecimal?,
    val periodBudgetCumulative: BigDecimal?,
    val nextReportForecast: BigDecimal?,
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
