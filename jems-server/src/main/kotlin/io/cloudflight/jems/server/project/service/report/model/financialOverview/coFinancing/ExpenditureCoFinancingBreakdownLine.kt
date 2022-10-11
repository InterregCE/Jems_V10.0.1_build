package io.cloudflight.jems.server.project.service.report.model.financialOverview.coFinancing

import java.math.BigDecimal

data class ExpenditureCoFinancingBreakdownLine(
    val fundId: Long? = null,
    val totalEligibleBudget: BigDecimal,
    val previouslyReported: BigDecimal,
    val previouslyPaid: BigDecimal,
    var currentReport: BigDecimal,
    var totalReportedSoFar: BigDecimal = BigDecimal.ZERO,
    var totalReportedSoFarPercentage: BigDecimal = BigDecimal.ZERO,
    var remainingBudget: BigDecimal = BigDecimal.ZERO,
)
