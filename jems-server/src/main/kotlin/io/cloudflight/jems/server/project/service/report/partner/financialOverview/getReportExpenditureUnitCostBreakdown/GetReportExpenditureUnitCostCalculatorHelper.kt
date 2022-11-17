package io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportExpenditureUnitCostBreakdown

import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ProjectPartnerReportExpenditureCost
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.unitCost.ExpenditureUnitCostBreakdownLine
import java.math.BigDecimal
import java.math.RoundingMode

fun Collection<ExpenditureUnitCostBreakdownLine>.fillInCurrent(current: Map<Long, BigDecimal>) = apply {
    forEach {
        it.currentReport = current.getOrDefault(it.reportUnitCostId, BigDecimal.ZERO)
    }
}

fun List<ExpenditureUnitCostBreakdownLine>.fillInOverviewFields() = apply {
    forEach { it.fillInOverviewFields() }
}

private fun emptyLine() = ExpenditureUnitCostBreakdownLine(
    reportUnitCostId = 0L,
    unitCostId = 0L,
    name = emptySet(),
    totalEligibleBudget = BigDecimal.ZERO,
    previouslyReported = BigDecimal.ZERO,
    currentReport = BigDecimal.ZERO,
)

fun List<ExpenditureUnitCostBreakdownLine>.sumUp() =
    fold(emptyLine()) { resultingTotalLine, unitCost ->
        resultingTotalLine.totalEligibleBudget += unitCost.totalEligibleBudget
        resultingTotalLine.previouslyReported += unitCost.previouslyReported
        resultingTotalLine.currentReport += unitCost.currentReport
        return@fold resultingTotalLine
    }.fillInOverviewFields()

private fun ExpenditureUnitCostBreakdownLine.fillInOverviewFields() = apply {
    totalReportedSoFar = previouslyReported.plus(currentReport)
    totalReportedSoFarPercentage = if (totalEligibleBudget.compareTo(BigDecimal.ZERO) == 0) BigDecimal.ZERO else
        totalReportedSoFar.multiply(BigDecimal.valueOf(100)).divide(totalEligibleBudget, 2, RoundingMode.HALF_UP)
    remainingBudget = totalEligibleBudget.minus(totalReportedSoFar)
}

fun Collection<ProjectPartnerReportExpenditureCost>.getCurrentForUnitCosts() =
    filter { it.unitCostId != null }
        .groupBy { it.unitCostId!! }
        .mapValues { it.value.sumOf { it.declaredAmountAfterSubmission!! } }
