package io.cloudflight.jems.server.project.service.report.project.financialOverview.getReportCoFinancingBreakdown

import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.coFinancing.CertificateCoFinancingBreakdown
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.coFinancing.CertificateCoFinancingBreakdownLine
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.coFinancing.ReportCertificateCoFinancing
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.coFinancing.ReportCertificateCoFinancingColumn
import java.math.BigDecimal
import java.math.RoundingMode

fun ReportCertificateCoFinancing.toLinesModel() = CertificateCoFinancingBreakdown(
    funds = totalsFromAF.funds.map {
        CertificateCoFinancingBreakdownLine(
            fundId = it.key,
            totalEligibleBudget = it.value,
            previouslyReported = previouslyReported.funds.getOrDefault(it.key, BigDecimal.ZERO),
            previouslyPaid = previouslyPaid.funds.getOrDefault(it.key, BigDecimal.ZERO),
            currentReport = currentlyReported.funds.getOrDefault(it.key, BigDecimal.ZERO),
        )
    },
    partnerContribution = CertificateCoFinancingBreakdownLine(
        totalEligibleBudget = totalsFromAF.partnerContribution,
        previouslyReported = previouslyReported.partnerContribution,
        previouslyPaid = previouslyPaid.partnerContribution,
        currentReport = currentlyReported.partnerContribution,
    ),
    publicContribution = CertificateCoFinancingBreakdownLine(
        totalEligibleBudget = totalsFromAF.publicContribution,
        previouslyReported = previouslyReported.publicContribution,
        previouslyPaid = previouslyPaid.publicContribution,
        currentReport = currentlyReported.publicContribution,
    ),
    automaticPublicContribution = CertificateCoFinancingBreakdownLine(
        totalEligibleBudget = totalsFromAF.automaticPublicContribution,
        previouslyReported = previouslyReported.automaticPublicContribution,
        previouslyPaid = previouslyPaid.automaticPublicContribution,
        currentReport = currentlyReported.automaticPublicContribution,
    ),
    privateContribution = CertificateCoFinancingBreakdownLine(
        totalEligibleBudget = totalsFromAF.privateContribution,
        previouslyReported = previouslyReported.privateContribution,
        previouslyPaid = previouslyPaid.privateContribution,
        currentReport = currentlyReported.privateContribution,
    ),
    total = CertificateCoFinancingBreakdownLine(
        totalEligibleBudget = totalsFromAF.sum,
        previouslyReported = previouslyReported.sum,
        previouslyPaid = previouslyPaid.sum,
        currentReport = currentlyReported.sum,
    ),
)

fun CertificateCoFinancingBreakdown.fillInCurrent(current: ReportCertificateCoFinancingColumn) = apply {
    funds.forEach { fund ->
        fund.currentReport = current.funds.getOrDefault(fund.fundId, BigDecimal.ZERO)
    }
    partnerContribution.currentReport = current.partnerContribution
    publicContribution.currentReport = current.publicContribution
    automaticPublicContribution.currentReport = current.automaticPublicContribution
    privateContribution.currentReport = current.privateContribution
    total.currentReport = current.sum
}

fun CertificateCoFinancingBreakdown.fillInOverviewFields() = apply {
    funds.forEach { it.fillInOverviewFields() }
    partnerContribution.fillInOverviewFields()
    publicContribution.fillInOverviewFields()
    automaticPublicContribution.fillInOverviewFields()
    privateContribution.fillInOverviewFields()
    total.fillInOverviewFields()
}

private fun CertificateCoFinancingBreakdownLine.fillInOverviewFields() = apply {
    totalReportedSoFar = previouslyReported.plus(currentReport)
    totalReportedSoFarPercentage = if (totalEligibleBudget.compareTo(BigDecimal.ZERO) == 0) BigDecimal.ZERO else
        totalReportedSoFar.multiply(BigDecimal.valueOf(100)).divide(totalEligibleBudget, 2, RoundingMode.HALF_UP)
    remainingBudget = totalEligibleBudget.minus(totalReportedSoFar)
}
