package io.cloudflight.jems.server.project.service.report.project.spfContributionClaim.updateSpfContributionClaim

import io.cloudflight.jems.server.project.service.report.model.project.spfContributionClaim.ProjectReportSpfContributionClaim
import io.cloudflight.jems.server.project.service.report.model.project.spfContributionClaim.ProjectReportSpfContributionClaimUpdate

interface UpdateSpfContributionClaimInteractor {

    fun update(
        projectId: Long,
        reportId: Long,
        toUpdate: List<ProjectReportSpfContributionClaimUpdate>
    ): List<ProjectReportSpfContributionClaim>
}
