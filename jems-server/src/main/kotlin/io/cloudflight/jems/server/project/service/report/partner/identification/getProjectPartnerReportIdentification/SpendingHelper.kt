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
    spendingProfile.differenceFromPlan = calculateDifferenceFromPlan(spendingProfile.periodDetail, currentTotal)
    spendingProfile.differenceFromPlanPercentage = calculateDifferenceFromPlanPercentage(
        spendingProfile.periodDetail,
        currentTotal
    )
}

fun calculateDifferenceFromPlan(periodDetail: ProjectPartnerReportPeriod?, currentTotal: BigDecimal): BigDecimal {
    return if (periodDetail != null && periodDetail.periodBudgetCumulative > BigDecimal.ZERO) {
        periodDetail.periodBudgetCumulative.minus(currentTotal)
    } else {
        BigDecimal.ZERO
    }
}

fun calculateDifferenceFromPlanPercentage(periodDetail: ProjectPartnerReportPeriod?, currentTotal: BigDecimal): BigDecimal {
    return if (periodDetail != null && periodDetail.periodBudgetCumulative > BigDecimal.ZERO) {
        currentTotal.multiply(BigDecimal.valueOf(100, 0))
            .divide(periodDetail.periodBudgetCumulative, 2, RoundingMode.HALF_UP)
    } else {
        BigDecimal.ZERO
    }
}
