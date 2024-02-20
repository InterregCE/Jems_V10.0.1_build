package io.cloudflight.jems.server.project.service.report.partner.identification.getProjectPartnerReportIdentification

import io.cloudflight.jems.server.project.service.report.model.partner.identification.ProjectPartnerReportIdentification
import io.cloudflight.jems.server.project.service.report.model.partner.identification.ProjectPartnerReportPeriod
import java.math.BigDecimal
import java.math.RoundingMode

fun ProjectPartnerReportIdentification.fillInCurrentAndPreviousReporting(
    currentReport: BigDecimal,
    previouslyReported: BigDecimal,
) = apply {
    spendingProfile.currentReport = currentReport
    spendingProfile.previouslyReported = previouslyReported

    val currentTotal = spendingProfile.previouslyReported.plus(spendingProfile.currentReport)
    spendingProfile.differenceFromPlan = calculateDifferenceFromPlan(spendingProfile.periodDetail?.periodBudgetCumulative, currentTotal)
    spendingProfile.differenceFromPlanPercentage = calculateDifferenceFromPlanPercentage(
        spendingProfile.periodDetail?.periodBudgetCumulative,
        currentTotal
    )
}

fun calculateDifferenceFromPlan(periodBudgetCumulative: BigDecimal?, currentTotal: BigDecimal): BigDecimal {
    return if (periodBudgetCumulative != null && periodBudgetCumulative > BigDecimal.ZERO) {
        periodBudgetCumulative.minus(currentTotal)
    } else {
        BigDecimal.ZERO
    }
}

fun calculateDifferenceFromPlanPercentage(periodBudgetCumulative: BigDecimal?, currentTotal: BigDecimal): BigDecimal {
    return if (periodBudgetCumulative != null && periodBudgetCumulative > BigDecimal.ZERO) {
        currentTotal.multiply(BigDecimal.valueOf(100, 0))
            .divide(periodBudgetCumulative, 2, RoundingMode.HALF_UP)
    } else {
        BigDecimal.ZERO
    }
}
