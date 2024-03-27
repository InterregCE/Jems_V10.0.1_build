package io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportExpenditureLumpSumBreakdown

import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ProjectPartnerReportExpenditureCost
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.control.ProjectPartnerReportExpenditureVerification
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.lumpSum.ExpenditureLumpSumBreakdownLine
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.lumpSum.ExpenditureLumpSumCurrent
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.lumpSum.ExpenditureLumpSumCurrentWithReIncluded
import java.math.BigDecimal
import java.math.RoundingMode

fun Collection<ExpenditureLumpSumBreakdownLine>.fillInCurrent(current: Map<Long, ExpenditureLumpSumCurrentWithReIncluded>) = apply {
    forEach {
        it.currentReport = current.get(it.reportLumpSumId)?.current ?: BigDecimal.ZERO
        it.currentReportReIncluded = current.get(it.reportLumpSumId)?.currentReIncluded ?: BigDecimal.ZERO
    }
}

private fun emptyLine() = ExpenditureLumpSumBreakdownLine(
    reportLumpSumId = 0L,
    lumpSumId = 0L,
    name = emptySet(),
    fastTrack = false,
    period = null,
    totalEligibleBudget = BigDecimal.ZERO,
    previouslyReported = BigDecimal.ZERO,
    previouslyPaid = BigDecimal.ZERO,
    currentReport = BigDecimal.ZERO,
    previouslyReportedParked = BigDecimal.ZERO,
    totalEligibleAfterControl = BigDecimal.ZERO,
    currentReportReIncluded = BigDecimal.ZERO,
    previouslyValidated = BigDecimal.ZERO,
)

fun List<ExpenditureLumpSumBreakdownLine>.sumUp() =
    fold(emptyLine()) { resultingTotalLine, lumpSum ->
        resultingTotalLine.totalEligibleBudget += lumpSum.totalEligibleBudget
        resultingTotalLine.previouslyReported += lumpSum.previouslyReported
        resultingTotalLine.previouslyPaid += lumpSum.previouslyPaid
        resultingTotalLine.currentReport += lumpSum.currentReport
        resultingTotalLine.totalEligibleAfterControl += lumpSum.totalEligibleAfterControl
        resultingTotalLine.previouslyReportedParked += lumpSum.previouslyReportedParked
        resultingTotalLine.currentReportReIncluded += lumpSum.currentReportReIncluded
        resultingTotalLine.previouslyValidated += lumpSum.previouslyValidated
        return@fold resultingTotalLine
    }

fun Collection<ProjectPartnerReportExpenditureCost>.getCurrentForLumpSums() =
    filter { it.lumpSumId != null }
        .groupBy { it.lumpSumId!! }
        // we can use pricePerUnit instead of declaredAmountAfterSubmission because for lumpSum currency rate is always 1
        .mapValues { ExpenditureLumpSumCurrentWithReIncluded(
            current = it.value.sumOf { it.pricePerUnit },
            currentReIncluded = it.value.filter { it.parkingMetadata != null }.sumOf { it.pricePerUnit }
        ) }

fun Collection<ProjectPartnerReportExpenditureVerification>.getAfterControlForLumpSums() =
    filter { it.lumpSumId != null }
        .groupBy { it.lumpSumId!! }
        .mapValues {
            ExpenditureLumpSumCurrent(
                current = it.value.sumOf { it.certifiedAmount },
                currentParked = it.value.filter { it.parked }.sumOf { it.declaredAmountAfterSubmission ?: BigDecimal.ZERO }
            )
        }
