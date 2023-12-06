package io.cloudflight.jems.server.project.service.report.project.spfContributionClaim

import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.coFinancing.ReportExpenditureCoFinancingColumn
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.coFinancing.ReportCertificateCoFinancingColumn
import io.cloudflight.jems.server.project.service.report.model.project.spfContributionClaim.ProjectReportSpfContributionClaim
import io.cloudflight.jems.server.project.service.report.model.project.spfContributionClaim.SpfPreviouslyReportedByContributionSource
import io.cloudflight.jems.server.project.service.report.model.project.verification.financialOverview.financingSource.FinancingSourceBreakdownLine
import java.math.BigDecimal

interface ProjectReportSpfContributionClaimPersistence {

    fun getSpfContributionClaimsFor(reportId: Long): List<ProjectReportSpfContributionClaim>

    fun updateContributionClaimReportedAmount(
        reportId: Long,
        toUpdate: Map<Long, BigDecimal>,
    ): List<ProjectReportSpfContributionClaim>

    fun getSpfContributionCumulative(reportIds: Set<Long>): SpfPreviouslyReportedByContributionSource
    fun getPreviouslyReportedSpfContributions(reportIds: Set<Long>): ReportExpenditureCoFinancingColumn

    fun getCurrentSpfContribution(reportId: Long): ReportCertificateCoFinancingColumn
    fun getCurrentSpfContributionSplit(reportId: Long): FinancingSourceBreakdownLine?

    fun resetSpfContributionClaims(reportId: Long)
}
