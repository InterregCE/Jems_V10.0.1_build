package io.cloudflight.jems.server.project.service.report.model.financialOverview.investments

import java.math.BigDecimal

data class ExpenditureInvestmentBreakdownLine(
    val investmentId: Long,
    val investmentNumber: Int,
    val workPackageNumber: Int,
    var totalEligibleBudget: BigDecimal,
    var previouslyReported: BigDecimal,
    var currentReport: BigDecimal,
    var totalReportedSoFar: BigDecimal = BigDecimal.ZERO,
    var totalReportedSoFarPercentage: BigDecimal = BigDecimal.ZERO,
    var remainingBudget: BigDecimal = BigDecimal.ZERO,
)
