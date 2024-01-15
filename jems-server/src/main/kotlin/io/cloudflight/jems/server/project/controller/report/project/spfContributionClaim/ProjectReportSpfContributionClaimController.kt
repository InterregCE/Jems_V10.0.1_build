package io.cloudflight.jems.server.project.controller.report.project.spfContributionClaim

import io.cloudflight.jems.api.project.dto.report.project.spfContribution.ProjectReportSpfContributionClaimDTO
import io.cloudflight.jems.api.project.dto.report.project.spfContribution.ProjectReportSpfContributionClaimUpdateDTO
import io.cloudflight.jems.api.project.report.project.ProjectReportSpfContributionApi
import io.cloudflight.jems.server.project.service.report.project.spfContributionClaim.getSpfContributionClaim.GetSpfContributionClaimInteractor
import io.cloudflight.jems.server.project.service.report.project.spfContributionClaim.updateSpfContributionClaim.UpdateSpfContributionClaimInteractor
import org.springframework.web.bind.annotation.RestController

@RestController
class ProjectReportSpfContributionClaimController(
    private val projectReportSpfContributionClaim: GetSpfContributionClaimInteractor,
    private val updateSpfContributionClaim: UpdateSpfContributionClaimInteractor,
): ProjectReportSpfContributionApi {
    override fun getContributionClaims(
        projectId: Long,
        reportId: Long
    ): List<ProjectReportSpfContributionClaimDTO> =
        projectReportSpfContributionClaim.getContributionClaims(projectId, reportId).toDtoList()


    override fun updateContributionClaims(
        projectId: Long,
        reportId: Long,
        toUpdate: List<ProjectReportSpfContributionClaimUpdateDTO>
    ): List<ProjectReportSpfContributionClaimDTO> {
        return updateSpfContributionClaim.update(projectId, reportId, toUpdate.toModelUpdateList()).toDtoList()
    }
}
