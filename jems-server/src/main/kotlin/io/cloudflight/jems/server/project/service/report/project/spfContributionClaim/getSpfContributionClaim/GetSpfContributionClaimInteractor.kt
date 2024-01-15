package io.cloudflight.jems.server.project.service.report.project.spfContributionClaim.getSpfContributionClaim

import io.cloudflight.jems.server.project.service.report.model.project.spfContributionClaim.ProjectReportSpfContributionClaim

interface GetSpfContributionClaimInteractor {

    fun getContributionClaims(projectId: Long, reportId: Long): List<ProjectReportSpfContributionClaim>
}
