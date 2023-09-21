package io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportExpenditureInvestementsBreakdown

import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ProjectPartnerReportExpenditureCost
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.control.ProjectPartnerReportExpenditureVerification
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.investments.ExpenditureInvestmentBreakdownLine
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.investments.ExpenditureInvestmentCurrent
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.investments.ExpenditureInvestmentCurrentWithReIncluded
import java.math.BigDecimal


fun Collection<ExpenditureInvestmentBreakdownLine>.fillInCurrent(current: Map<Long, ExpenditureInvestmentCurrentWithReIncluded>) = apply {
    forEach {
        it.currentReport = current.get(it.reportInvestmentId)?.current ?: BigDecimal.ZERO
        it.currentReportReIncluded = current.get(it.reportInvestmentId)?.currentReIncluded ?: BigDecimal.ZERO
    }
}

private fun emptyLine() = ExpenditureInvestmentBreakdownLine(
    reportInvestmentId = 0L,
    investmentId = 0L,
    investmentNumber = 0,
    workPackageNumber = 0,
    title = emptySet(),
    deactivated = false,
    totalEligibleBudget = BigDecimal.ZERO,
    previouslyReported = BigDecimal.ZERO,
    previouslyReportedParked = BigDecimal.ZERO,
    currentReport = BigDecimal.ZERO,
    currentReportReIncluded = BigDecimal.ZERO,
    totalEligibleAfterControl = BigDecimal.ZERO,
    totalReportedSoFar = BigDecimal.ZERO,
    totalReportedSoFarPercentage = BigDecimal.ZERO,
    remainingBudget = BigDecimal.ZERO,
    previouslyValidated = BigDecimal.ZERO,
)

fun List<ExpenditureInvestmentBreakdownLine>.sumUp() =
    fold(emptyLine()) { resultingTotalLine, investment ->
        resultingTotalLine.totalEligibleBudget += investment.totalEligibleBudget
        resultingTotalLine.previouslyReported += investment.previouslyReported
        resultingTotalLine.currentReport += investment.currentReport
        resultingTotalLine.totalEligibleAfterControl += investment.totalEligibleAfterControl
        resultingTotalLine.previouslyReportedParked += investment.previouslyReportedParked
        resultingTotalLine.currentReportReIncluded += investment.currentReportReIncluded
        resultingTotalLine.previouslyValidated += investment.previouslyValidated
        return@fold resultingTotalLine
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
