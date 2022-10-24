package io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportExpenditureInvestementsBreakdown

import io.cloudflight.jems.server.project.service.report.model.expenditure.ProjectPartnerReportExpenditureCost
import io.cloudflight.jems.server.project.service.report.model.financialOverview.investments.ExpenditureInvestmentBreakdownLine
import java.math.BigDecimal
import java.math.RoundingMode


fun Collection<ExpenditureInvestmentBreakdownLine>.fillInCurrent(current: Map<Long, BigDecimal>) = apply {
    forEach {
        it.currentReport = current.getOrDefault(it.investmentId, BigDecimal.ZERO)
    }
}

fun List<ExpenditureInvestmentBreakdownLine>.fillInOverviewFields() = apply {
    forEach { it.fillInOverviewFields() }
}

private fun emptyLine() = ExpenditureInvestmentBreakdownLine(
    investmentId = 0L,
    investmentNumber = 0,
    workPackageNumber = 0,
    totalEligibleBudget = BigDecimal.ZERO,
    previouslyReported = BigDecimal.ZERO,
    currentReport = BigDecimal.ZERO,
    totalReportedSoFar = BigDecimal.ZERO,
    totalReportedSoFarPercentage = BigDecimal.ZERO,
    remainingBudget = BigDecimal.ZERO
)

fun List<ExpenditureInvestmentBreakdownLine>.sumUp() =
    fold(emptyLine()) { resultingTotalLine, investment ->
        resultingTotalLine.totalEligibleBudget += investment.totalEligibleBudget
        resultingTotalLine.previouslyReported += investment.previouslyReported
        resultingTotalLine.currentReport += investment.currentReport
        return@fold resultingTotalLine
    }.fillInOverviewFields()

private fun ExpenditureInvestmentBreakdownLine.fillInOverviewFields() = apply {
    totalReportedSoFar = previouslyReported.plus(currentReport)
    totalReportedSoFarPercentage = if (totalEligibleBudget.compareTo(BigDecimal.ZERO) == 0) BigDecimal.ZERO else
        totalReportedSoFar.multiply(BigDecimal.valueOf(100)).divide(totalEligibleBudget, 2, RoundingMode.HALF_UP)
    remainingBudget = totalEligibleBudget.minus(totalReportedSoFar)
}

fun Collection<ProjectPartnerReportExpenditureCost>.getCurrentForInvestments() =
    filter { it.investmentId != null }
        .groupBy { it.investmentId!! }
        .mapValues { it.value
            .filter{ it.declaredAmountAfterSubmission != null }
            .sumOf { it.declaredAmountAfterSubmission!! }
        }
