package io.cloudflight.jems.server.project.service.report.model.financialOverview.costCategory

import java.math.BigDecimal

data class ReportExpenditureCostCategoryLine(
    val flatRate: Int?,

    val totalEligibleBudget: BigDecimal,
    val previouslyReported: BigDecimal,
    val currentReport: BigDecimal,
    val totalReportedSoFar: BigDecimal,
    val totalReportedSoFarPercentage: BigDecimal,
    val remainingBudget: BigDecimal,
)
