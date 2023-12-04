package io.cloudflight.jems.server.project.repository.report.project.spfContributionClaim

import io.cloudflight.jems.server.project.entity.report.project.ProjectReportSpfContributionClaimEntity
import io.cloudflight.jems.server.project.service.report.model.project.spfContributionClaim.ProjectReportSpfContributionClaim
import io.cloudflight.jems.server.project.service.report.model.project.spfContributionClaim.ProjectReportSpfContributionClaimUpdate
import io.cloudflight.jems.server.project.service.report.model.project.spfContributionClaim.SpfPreviouslyReportedByContributionSource
import io.cloudflight.jems.server.project.service.report.project.spfContributionClaim.ProjectReportSpfContributionClaimPersistence
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal


@Repository
class ProjectReportSpfContributionClaimPersistenceProvider(
   private val spfContributionClaimRepository: ProjectReportSpfContributionClaimRepository
): ProjectReportSpfContributionClaimPersistence {

    @Transactional(readOnly = true)
    override fun getSpfContributionClaimsFor(reportId: Long): List<ProjectReportSpfContributionClaim> =
        spfContributionClaimRepository.getAllByReportEntityId(reportId).map { it.toModel() }


    @Transactional
    override fun updateContributionClaimReportedAmount(
        reportId: Long,
        toUpdate: Map<Long, BigDecimal>
    ): List<ProjectReportSpfContributionClaim> {
        val contributions = spfContributionClaimRepository.getAllByReportEntityId(reportId)
        contributions.updateWith(toUpdate)
        return contributions.map { it.toModel() }
    }

    @Transactional(readOnly = true)
    override fun getPreviouslyReportedContributionForProject(projectId: Long): SpfPreviouslyReportedByContributionSource {
        val previouslyReportedByContributionSource = spfContributionClaimRepository.getPreviouslyReportedContributionAmount(projectId)

        val finances = previouslyReportedByContributionSource.filter { it.programmeFundId != null }
            .associateBy{ it.programmeFundId!! }
        val partnerContributions =
            previouslyReportedByContributionSource.filter { it.applicationFormPartnerContributionId != null }
                .associateBy{ it.applicationFormPartnerContributionId!! }

        return SpfPreviouslyReportedByContributionSource(
            finances = finances,
            partnerContributions = partnerContributions
        )
    }

    @Transactional
    override fun resetSpfContributionClaims(reportId: Long): List<ProjectReportSpfContributionClaim> {
        val contributions = spfContributionClaimRepository.getAllByReportEntityId(reportId)
        contributions.forEach { contributionClaim ->
            contributionClaim.currentlyReported = BigDecimal.ZERO
        }
        return contributions.map { it.toModel() }
    }

    private fun List<ProjectReportSpfContributionClaimEntity>.updateWith(toUpdate: Map<Long, BigDecimal>) {
        this.forEach { contributionClaim ->
            contributionClaim.currentlyReported = toUpdate[contributionClaim.id] ?: contributionClaim.currentlyReported
        }
    }

}


