package io.cloudflight.jems.server.project.service.report.model.project.identification

import java.math.BigDecimal

data class SpendingProfileTotal(
    var periodBudget: BigDecimal,
    var periodBudgetCumulative: BigDecimal,
    var differenceFromPlan: BigDecimal,
    var differenceFromPlanPercentage: BigDecimal,
    var nextReportForecast: BigDecimal,

    var totalEligibleBudget: BigDecimal,
    var currentReport: BigDecimal,
    var previouslyReported: BigDecimal,
    var totalReportedSoFar: BigDecimal,
    var totalReportedSoFarPercentage: BigDecimal,
    var remainingBudget: BigDecimal,
)
