package io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportExpenditureInvestementsBreakdown

import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ProjectPartnerReportExpenditureCost
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.control.ProjectPartnerReportExpenditureVerification
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.investments.ExpenditureInvestmentBreakdownLine
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.investments.ExpenditureInvestmentCurrent
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.investments.ExpenditureInvestmentCurrentWithReIncluded
import java.math.BigDecimal
import java.math.RoundingMode


fun Collection<ExpenditureInvestmentBreakdownLine>.fillInCurrent(current: Map<Long, ExpenditureInvestmentCurrentWithReIncluded>) = apply {
    forEach {
        it.currentReport = current.get(it.reportInvestmentId)?.current ?: BigDecimal.ZERO
        it.currentReportReIncluded = current.get(it.reportInvestmentId)?.currentReIncluded ?: BigDecimal.ZERO
    }
}

fun List<ExpenditureInvestmentBreakdownLine>.fillInOverviewFields() = apply {
    forEach { it.fillInOverviewFields() }
}

private fun emptyLine() = ExpenditureInvestmentBreakdownLine(
    reportInvestmentId = 0L,
    investmentId = 0L,
    investmentNumber = 0,
    workPackageNumber = 0,
    title = emptySet(),
    totalEligibleBudget = BigDecimal.ZERO,
    previouslyReported = BigDecimal.ZERO,
    previouslyReportedParked = BigDecimal.ZERO,
    currentReport = BigDecimal.ZERO,
    currentReportReIncluded = BigDecimal.ZERO,
    totalEligibleAfterControl = BigDecimal.ZERO,
    totalReportedSoFar = BigDecimal.ZERO,
    totalReportedSoFarPercentage = BigDecimal.ZERO,
    remainingBudget = BigDecimal.ZERO
)

fun List<ExpenditureInvestmentBreakdownLine>.sumUp() =
    fold(emptyLine()) { resultingTotalLine, investment ->
        resultingTotalLine.totalEligibleBudget += investment.totalEligibleBudget
        resultingTotalLine.previouslyReported += investment.previouslyReported
        resultingTotalLine.currentReport += investment.currentReport
        resultingTotalLine.totalEligibleAfterControl += investment.totalEligibleAfterControl
        resultingTotalLine.previouslyReportedParked += investment.previouslyReportedParked
        resultingTotalLine.currentReportReIncluded += investment.currentReportReIncluded
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
        .mapValues {
            ExpenditureInvestmentCurrentWithReIncluded(
                current = it.value.sumOf { it.declaredAmountAfterSubmission ?: BigDecimal.ZERO },
                currentReIncluded = it.value.filter { it.parkingMetadata != null }
                    .sumOf { it.declaredAmountAfterSubmission ?: BigDecimal.ZERO }
            )
        }

fun Collection<ProjectPartnerReportExpenditureVerification>.getAfterControlForInvestments() =
    filter { it.investmentId != null }
        .groupBy { it.investmentId!! }
        .mapValues {
            ExpenditureInvestmentCurrent(
                current = it.value.sumOf { it.certifiedAmount },
                currentParked = it.value.filter { it.parked }.sumOf { it.declaredAmountAfterSubmission ?: BigDecimal.ZERO })
        }
