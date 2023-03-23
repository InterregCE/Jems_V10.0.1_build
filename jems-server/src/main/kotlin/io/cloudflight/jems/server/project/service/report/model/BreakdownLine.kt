package io.cloudflight.jems.server.project.service.report.model

import java.math.BigDecimal

interface BreakdownLine {
    val totalEligibleBudget: BigDecimal
    val previouslyReported: BigDecimal
    val currentReport: BigDecimal

    var totalReportedSoFar: BigDecimal
    var totalReportedSoFarPercentage: BigDecimal
    var remainingBudget: BigDecimal
}
