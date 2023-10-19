package io.cloudflight.jems.server.project.service.report.project.financialOverview.getReportCoFinancingBreakdown

import io.cloudflight.jems.server.project.service.report.fillInOverviewFields
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.coFinancing.CertificateCoFinancingBreakdown
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.coFinancing.CertificateCoFinancingBreakdownLine
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.coFinancing.ReportCertificateCoFinancing
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.coFinancing.ReportCertificateCoFinancingColumn
import java.math.BigDecimal

fun ReportCertificateCoFinancing.toLinesModel() = CertificateCoFinancingBreakdown(
    funds = totalsFromAF.funds.map {
        CertificateCoFinancingBreakdownLine(
            fundId = it.key,
            totalEligibleBudget = it.value,
            previouslyReported = previouslyReported.funds.getOrDefault(it.key, BigDecimal.ZERO),
            currentReport = currentlyReported.funds.getOrDefault(it.key, BigDecimal.ZERO),
            previouslyVerified = previouslyVerified.funds.getOrDefault(it.key, BigDecimal.ZERO),
            currentVerified = currentVerified.funds.getOrDefault(it.key, BigDecimal.ZERO),
            previouslyPaid = previouslyPaid.funds.getOrDefault(it.key, BigDecimal.ZERO),
        )
    }.sortedWith(compareBy(nullsLast()) { it.fundId }),
    partnerContribution = CertificateCoFinancingBreakdownLine(
        totalEligibleBudget = totalsFromAF.partnerContribution,
        previouslyReported = previouslyReported.partnerContribution,
        currentReport = currentlyReported.partnerContribution,
        previouslyVerified = previouslyVerified.partnerContribution,
        currentVerified = currentVerified.partnerContribution,
        previouslyPaid = previouslyPaid.partnerContribution,
    ),
    publicContribution = CertificateCoFinancingBreakdownLine(
        totalEligibleBudget = totalsFromAF.publicContribution,
        previouslyReported = previouslyReported.publicContribution,
        currentReport = currentlyReported.publicContribution,
        previouslyVerified = previouslyVerified.publicContribution,
        currentVerified = currentVerified.publicContribution,
        previouslyPaid = previouslyPaid.publicContribution,
    ),
    automaticPublicContribution = CertificateCoFinancingBreakdownLine(
        totalEligibleBudget = totalsFromAF.automaticPublicContribution,
        previouslyReported = previouslyReported.automaticPublicContribution,
        currentReport = currentlyReported.automaticPublicContribution,
        previouslyVerified = previouslyVerified.automaticPublicContribution,
        currentVerified = currentVerified.automaticPublicContribution,
        previouslyPaid = previouslyPaid.automaticPublicContribution,
    ),
    privateContribution = CertificateCoFinancingBreakdownLine(
        totalEligibleBudget = totalsFromAF.privateContribution,
        previouslyReported = previouslyReported.privateContribution,
        currentReport = currentlyReported.privateContribution,
        previouslyVerified = previouslyVerified.privateContribution,
        currentVerified = currentVerified.privateContribution,
        previouslyPaid = previouslyPaid.privateContribution,
    ),
    total = CertificateCoFinancingBreakdownLine(
        totalEligibleBudget = totalsFromAF.sum,
        previouslyReported = previouslyReported.sum,
        currentReport = currentlyReported.sum,
        previouslyVerified = previouslyVerified.sum,
        currentVerified = currentVerified.sum,
        previouslyPaid = previouslyPaid.sum,
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
    funds.fillInOverviewFields()
    partnerContribution.fillInOverviewFields()
    publicContribution.fillInOverviewFields()
    automaticPublicContribution.fillInOverviewFields()
    privateContribution.fillInOverviewFields()
    total.fillInOverviewFields()
}
