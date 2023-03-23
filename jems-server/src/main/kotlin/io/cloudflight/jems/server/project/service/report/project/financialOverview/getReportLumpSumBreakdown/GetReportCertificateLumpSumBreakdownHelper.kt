package io.cloudflight.jems.server.project.service.report.project.financialOverview.getReportLumpSumBreakdown

import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.lumpSum.CertificateLumpSumBreakdownLine
import java.math.BigDecimal

fun Collection<CertificateLumpSumBreakdownLine>.fillInCurrent(current: Map<Int, BigDecimal>) = apply {
    forEach {
        it.currentReport = current.get(it.orderNr) ?: BigDecimal.ZERO
    }
}

private fun emptyLine() = CertificateLumpSumBreakdownLine(
    reportLumpSumId = 0L,
    lumpSumId = 0L,
    period = null,
    orderNr = 0,
    name = emptySet(),
    totalEligibleBudget = BigDecimal.ZERO,
    previouslyReported = BigDecimal.ZERO,
    previouslyPaid = BigDecimal.ZERO,
    currentReport = BigDecimal.ZERO,
    totalReportedSoFar = BigDecimal.ZERO,
    totalReportedSoFarPercentage = BigDecimal.ZERO,
    remainingBudget = BigDecimal.ZERO,
)

fun List<CertificateLumpSumBreakdownLine>.sumUp() =
    fold(emptyLine()) { resultingTotalLine, lumpSum ->
        resultingTotalLine.totalEligibleBudget += lumpSum.totalEligibleBudget
        resultingTotalLine.previouslyReported += lumpSum.previouslyReported
        resultingTotalLine.previouslyPaid += lumpSum.previouslyPaid
        resultingTotalLine.currentReport += lumpSum.currentReport
        return@fold resultingTotalLine
    }
