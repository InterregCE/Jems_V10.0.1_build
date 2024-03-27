package io.cloudflight.jems.server.project.service.report

import io.cloudflight.jems.server.project.service.report.model.BreakdownLine
import io.cloudflight.jems.server.project.service.report.project.verification.financialOverview.getFinancingSourceBreakdown.isZero
import java.math.BigDecimal
import java.math.RoundingMode

fun <T : BreakdownLine> T.fillInOverviewFields() = apply {
    totalReportedSoFar = previouslyReported.plus(currentReport)
    totalReportedSoFarPercentage = totalReportedSoFar.percentageOf(totalEligibleBudget) ?: BigDecimal.ZERO
    remainingBudget = totalEligibleBudget.minus(totalReportedSoFar)
}

fun <T : BreakdownLine> List<T>.fillInOverviewFields() = onEach { it.fillInOverviewFields() }

fun BigDecimal.percentageOf(total: BigDecimal): BigDecimal? =
    if (total.isZero()) {
        if (this.isZero()) BigDecimal.valueOf(100L) else null
    } else
        this.multiply(BigDecimal.valueOf(100)).divide(total, 2, RoundingMode.HALF_UP)
