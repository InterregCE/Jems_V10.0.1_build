package io.cloudflight.jems.server.project.service.report.model.financialOverview

import java.math.BigDecimal

data class ExpenditureCostCategoryBreakdownLine(
    val flatRate: Int?,

    val totalEligibleBudget: BigDecimal,
    val previouslyReported: BigDecimal,
    var currentReport: BigDecimal,
    var totalReportedSoFar: BigDecimal = BigDecimal.ZERO,
    var totalReportedSoFarPercentage: BigDecimal = BigDecimal.ZERO,
    var remainingBudget: BigDecimal = BigDecimal.ZERO,
)
