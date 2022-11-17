package io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportExpenditureLumpSumBreakdown

import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ProjectPartnerReportExpenditureCost
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.lumpSum.ExpenditureLumpSumBreakdownLine
import java.math.BigDecimal
import java.math.RoundingMode

fun Collection<ExpenditureLumpSumBreakdownLine>.fillInCurrent(current: Map<Long, BigDecimal>) = apply {
    forEach {
        it.currentReport = current.getOrDefault(it.reportLumpSumId, BigDecimal.ZERO)
    }
}

fun List<ExpenditureLumpSumBreakdownLine>.fillInOverviewFields() = apply {
    forEach { it.fillInOverviewFields() }
}

private fun emptyLine() = ExpenditureLumpSumBreakdownLine(
    reportLumpSumId = 0L,
    lumpSumId = 0L,
    period = null,
    name = emptySet(),
    totalEligibleBudget = BigDecimal.ZERO,
    previouslyReported = BigDecimal.ZERO,
    previouslyPaid = BigDecimal.ZERO,
    currentReport = BigDecimal.ZERO,
)

fun List<ExpenditureLumpSumBreakdownLine>.sumUp() =
    fold(emptyLine()) { resultingTotalLine, lumpSum ->
        resultingTotalLine.totalEligibleBudget += lumpSum.totalEligibleBudget
        resultingTotalLine.previouslyReported += lumpSum.previouslyReported
        resultingTotalLine.previouslyPaid += lumpSum.previouslyPaid
        resultingTotalLine.currentReport += lumpSum.currentReport
        return@fold resultingTotalLine
    }.fillInOverviewFields()

private fun ExpenditureLumpSumBreakdownLine.fillInOverviewFields() = apply {
    totalReportedSoFar = previouslyReported.plus(currentReport)
    totalReportedSoFarPercentage = if (totalEligibleBudget.compareTo(BigDecimal.ZERO) == 0) BigDecimal.ZERO else
        totalReportedSoFar.multiply(BigDecimal.valueOf(100)).divide(totalEligibleBudget, 2, RoundingMode.HALF_UP)
    remainingBudget = totalEligibleBudget.minus(totalReportedSoFar)
}

fun Collection<ProjectPartnerReportExpenditureCost>.getCurrentForLumpSums() =
    filter { it.lumpSumId != null }
        .groupBy { it.lumpSumId!! }
        // we can use pricePerUnit instead of declaredAmountAfterSubmission because for lumpSum currency rate is always 1
        .mapValues { it.value.sumOf { it.pricePerUnit } }
