package io.cloudflight.jems.server.project.repository.report.project.spfContributionClaim

import io.cloudflight.jems.server.project.entity.report.project.ProjectReportSpfContributionClaimEntity
import io.cloudflight.jems.server.project.service.report.model.project.spfContributionClaim.SpfPreviouslyReportedContributionRow
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface ProjectReportSpfContributionClaimRepository: JpaRepository<ProjectReportSpfContributionClaimEntity, Long> {

    fun getAllByReportEntityId(reportId: Long): List<ProjectReportSpfContributionClaimEntity>

    @Query(
        """
         SELECT new io.cloudflight.jems.server.project.service.report.model.project.spfContributionClaim.SpfPreviouslyReportedContributionRow(
                contributionClaim.id,
                contributionClaim.programmeFund.id,
                contributionClaim.applicationFormPartnerContributionId,
                COALESCE(SUM(contributionClaim.currentlyReported), 0)
            )
            FROM #{#entityName} as contributionClaim
            WHERE contributionClaim.reportEntity.projectId = :projectId
            GROUP BY contributionClaim.programmeFund.id, contributionClaim.applicationFormPartnerContributionId
    """
    )
    fun getPreviouslyReportedContributionAmount(projectId: Long): List<SpfPreviouslyReportedContributionRow>
}
