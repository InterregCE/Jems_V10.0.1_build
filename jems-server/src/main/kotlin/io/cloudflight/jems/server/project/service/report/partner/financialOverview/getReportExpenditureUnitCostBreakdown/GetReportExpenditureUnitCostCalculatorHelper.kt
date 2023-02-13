package io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportExpenditureUnitCostBreakdown

import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ProjectPartnerReportExpenditureCost
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.control.ProjectPartnerReportExpenditureVerification
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.unitCost.ExpenditureUnitCostBreakdownLine
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.unitCost.ExpenditureUnitCostCurrent
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.unitCost.ExpenditureUnitCostCurrentWithReIncluded
import java.math.BigDecimal
import java.math.RoundingMode

fun Collection<ExpenditureUnitCostBreakdownLine>.fillInCurrent(current: Map<Long, ExpenditureUnitCostCurrentWithReIncluded>) = apply {
    forEach {
        it.currentReport = current.get(it.reportUnitCostId)?.current ?: BigDecimal.ZERO
        it.currentReportReIncluded = current.get(it.reportUnitCostId)?.currentReIncluded ?: BigDecimal.ZERO
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
    previouslyReportedParked = BigDecimal.ZERO,
    currentReport = BigDecimal.ZERO,
    currentReportReIncluded = BigDecimal.ZERO,
    totalEligibleAfterControl = BigDecimal.ZERO,
)

fun List<ExpenditureUnitCostBreakdownLine>.sumUp() =
    fold(emptyLine()) { resultingTotalLine, unitCost ->
        resultingTotalLine.totalEligibleBudget += unitCost.totalEligibleBudget
        resultingTotalLine.previouslyReported += unitCost.previouslyReported
        resultingTotalLine.currentReport += unitCost.currentReport
        resultingTotalLine.totalEligibleAfterControl += unitCost.totalEligibleAfterControl
        resultingTotalLine.previouslyReportedParked += unitCost.previouslyReportedParked
        resultingTotalLine.currentReportReIncluded += unitCost.currentReportReIncluded
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
        .mapValues { ExpenditureUnitCostCurrentWithReIncluded(
            current = it.value.sumOf { it.declaredAmountAfterSubmission!! },
            currentReIncluded = it.value.filter { it.parkingMetadata != null }.sumOf { it.declaredAmountAfterSubmission!! }
        ) }

fun Collection<ProjectPartnerReportExpenditureVerification>.getAfterControlForUnitCosts() =
    filter { it.unitCostId != null }
        .groupBy { it.unitCostId!! }
        .mapValues {
            ExpenditureUnitCostCurrent(
                current = it.value.sumOf { it.certifiedAmount },
                currentParked = it.value.filter { it.parked }.sumOf { it.declaredAmountAfterSubmission ?: BigDecimal.ZERO }
            )
        }
