package io.cloudflight.jems.server.project.service.report.project.financialOverview.getReportUnitCostBreakdown

import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.unitCost.CertificateUnitCostBreakdownLine
import java.math.BigDecimal


fun Collection<CertificateUnitCostBreakdownLine>.fillInCurrent(current: Map<Long, BigDecimal>) = apply {
    forEach {
        it.currentReport = current.get(it.unitCostId) ?: BigDecimal.ZERO
    }
}

private fun emptyLine() = CertificateUnitCostBreakdownLine(
    reportUnitCostId = 0L,
    unitCostId = 0L,
    name = emptySet(),
    totalEligibleBudget = BigDecimal.ZERO,
    previouslyReported = BigDecimal.ZERO,
    currentReport = BigDecimal.ZERO,
    previouslyVerified = BigDecimal.ZERO,
    currentVerified = BigDecimal.ZERO,
)

fun List<CertificateUnitCostBreakdownLine>.sumUp() =
    fold(emptyLine()) { resultingTotalLine, lumpSum ->
        resultingTotalLine.totalEligibleBudget += lumpSum.totalEligibleBudget
        resultingTotalLine.previouslyReported += lumpSum.previouslyReported
        resultingTotalLine.currentReport += lumpSum.currentReport
        resultingTotalLine.previouslyVerified += lumpSum.previouslyVerified
        resultingTotalLine.currentVerified += lumpSum.currentVerified
        return@fold resultingTotalLine
    }
