package io.cloudflight.jems.server.project.repository.report.project.spfContributionClaim

import io.cloudflight.jems.server.project.entity.report.project.ProjectReportSpfContributionClaimEntity
import io.cloudflight.jems.server.project.service.report.model.project.spfContributionClaim.SpfPreviouslyReportedContributionRow
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface ProjectReportSpfContributionClaimRepository: JpaRepository<ProjectReportSpfContributionClaimEntity, Long> {

    fun getAllByReportEntityIdIn(reportIds: Set<Long>): List<ProjectReportSpfContributionClaimEntity>

    @Query("""
         SELECT new io.cloudflight.jems.server.project.service.report.model.project.spfContributionClaim.SpfPreviouslyReportedContributionRow(
                contributionClaim.id,
                contributionClaim.programmeFund.id,
                contributionClaim.applicationFormPartnerContributionId,
                contributionClaim.sourceOfContribution,
                contributionClaim.legalStatus,
                COALESCE(SUM(contributionClaim.currentlyReported), 0)
            )
            FROM #{#entityName} as contributionClaim
            WHERE contributionClaim.reportEntity.id IN :reportIds
            GROUP BY contributionClaim.programmeFund.id, contributionClaim.applicationFormPartnerContributionId
    """)
    fun getPreviouslyReportedContributionAmount(reportIds: Set<Long>): List<SpfPreviouslyReportedContributionRow>

}
