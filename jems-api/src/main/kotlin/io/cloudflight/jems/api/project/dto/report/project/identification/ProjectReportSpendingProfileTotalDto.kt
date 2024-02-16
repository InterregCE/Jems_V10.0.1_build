package io.cloudflight.jems.api.project.dto.report.project.identification

import java.math.BigDecimal

data class ProjectReportSpendingProfileTotalDto(
    val periodBudget: BigDecimal,
    val periodBudgetCumulative: BigDecimal,
    val differenceFromPlan: BigDecimal,
    val differenceFromPlanPercentage: BigDecimal,
    val nextReportForecast: BigDecimal,

    val totalEligibleBudget: BigDecimal,
    val currentReport: BigDecimal,
    val previouslyReported: BigDecimal,
    val totalReportedSoFar: BigDecimal,
    val totalReportedSoFarPercentage: BigDecimal,
    val remainingBudget: BigDecimal,
)
