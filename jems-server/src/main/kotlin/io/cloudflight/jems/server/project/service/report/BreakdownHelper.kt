package io.cloudflight.jems.server.project.service.report

import io.cloudflight.jems.server.project.service.report.model.BreakdownLine
import java.math.BigDecimal
import java.math.RoundingMode

fun <T : BreakdownLine> T.fillInOverviewFields() = apply {
    totalReportedSoFar = previouslyReported.plus(currentReport)
    totalReportedSoFarPercentage = totalReportedSoFar.percentageOf(totalEligibleBudget) ?: BigDecimal.ZERO
    remainingBudget = totalEligibleBudget.minus(totalReportedSoFar)
}

fun <T : BreakdownLine> List<T>.fillInOverviewFields() = onEach { it.fillInOverviewFields() }

fun BigDecimal.percentageOf(total: BigDecimal): BigDecimal? =
    if (total.compareTo(BigDecimal.ZERO) == 0) null
    else this.multiply(BigDecimal.valueOf(100)).divide(total, 2, RoundingMode.HALF_UP)
