package io.cloudflight.jems.server.project.service.report.project.spfContributionClaim

import io.cloudflight.jems.server.project.service.report.model.project.spfContributionClaim.ProjectReportSpfContributionClaim
import io.cloudflight.jems.server.project.service.report.model.project.spfContributionClaim.SpfPreviouslyReportedByContributionSource
import java.math.BigDecimal

interface ProjectReportSpfContributionClaimPersistence {

   fun getSpfContributionClaimsFor(reportId: Long): List<ProjectReportSpfContributionClaim>

   fun updateContributionClaimReportedAmount(reportId: Long, toUpdate: Map<Long, BigDecimal>): List<ProjectReportSpfContributionClaim>

   fun getPreviouslyReportedContributionForProject(projectId: Long): SpfPreviouslyReportedByContributionSource

   fun resetSpfContributionClaims(reportId: Long): List<ProjectReportSpfContributionClaim>
}
