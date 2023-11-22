package io.cloudflight.jems.server.project.service.report.project.spfContributionClaim.getSpfContributionClaim

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanRetrieveProjectReport
import io.cloudflight.jems.server.project.repository.report.project.spfContributionClaim.ProjectReportSpfContributionClaimPersistenceProvider
import io.cloudflight.jems.server.project.service.report.model.project.spfContributionClaim.ProjectReportSpfContributionClaim
import io.cloudflight.jems.server.project.service.report.project.spfContributionClaim.fillInTotalReportedSoFar
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetSpfContributionClaim(
    private val projectReportSpfContributionClaimPersistenceProvider: ProjectReportSpfContributionClaimPersistenceProvider
): GetSpfContributionClaimInteractor {

    @Transactional
    @CanRetrieveProjectReport
    @ExceptionWrapper(GetSpfContributionClaimException::class)
    override fun getContributionClaims(projectId: Long,reportId: Long): List<ProjectReportSpfContributionClaim> =
        projectReportSpfContributionClaimPersistenceProvider.getSpfContributionClaimsFor(reportId).also {
            it.fillInTotalReportedSoFar()
        }

}
