package io.cloudflight.jems.server.project.service.report.partner.identification.getProjectPartnerReportIdentification

import io.cloudflight.jems.server.project.service.report.model.identification.ProjectPartnerReportIdentification
import java.math.BigDecimal
import java.math.RoundingMode

fun ProjectPartnerReportIdentification.fillInCurrentAndPreviousReporting(
    currentReport: BigDecimal,
    previouslyReported: BigDecimal,
) = apply {
    spendingProfile.currentReport = currentReport
    spendingProfile.previouslyReported = previouslyReported
    val currentTotal = spendingProfile.previouslyReported.plus(spendingProfile.currentReport)

    if (spendingProfile.periodDetail != null && spendingProfile.periodDetail.periodBudgetCumulative > BigDecimal.ZERO) {
        val periodBudgetCumulative = spendingProfile.periodDetail.periodBudgetCumulative
        spendingProfile.differenceFromPlan = periodBudgetCumulative
            .minus(currentTotal)
        spendingProfile.differenceFromPlanPercentage = currentTotal.multiply(BigDecimal.valueOf(100, 0))
            .divide(periodBudgetCumulative, 2, RoundingMode.HALF_UP)
    }
}
