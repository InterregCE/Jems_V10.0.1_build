package io.cloudflight.jems.server.project.repository.report.project.spfContributionClaim

import io.cloudflight.jems.server.programme.repository.fund.toModel
import io.cloudflight.jems.server.project.entity.report.project.ProjectReportSpfContributionClaimEntity
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerContributionStatus
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.coFinancing.ReportExpenditureCoFinancingColumn
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.coFinancing.ReportCertificateCoFinancingColumn
import io.cloudflight.jems.server.project.service.report.model.project.spfContributionClaim.ProjectReportSpfContributionClaim
import io.cloudflight.jems.server.project.service.report.model.project.spfContributionClaim.SpfPreviouslyReportedByContributionSource
import io.cloudflight.jems.server.project.service.report.model.project.verification.financialOverview.financingSource.FinancingSourceBreakdownLine
import io.cloudflight.jems.server.project.service.report.project.spfContributionClaim.ProjectReportSpfContributionClaimPersistence
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal


@Repository
class ProjectReportSpfContributionClaimPersistenceProvider(
   private val spfContributionClaimRepository: ProjectReportSpfContributionClaimRepository
): ProjectReportSpfContributionClaimPersistence {

    companion object {
        private val emptySplit = ReportCertificateCoFinancingColumn(
            funds = emptyMap(),
            partnerContribution = BigDecimal.ZERO,
            publicContribution = BigDecimal.ZERO,
            automaticPublicContribution = BigDecimal.ZERO,
            privateContribution = BigDecimal.ZERO,
            sum = BigDecimal.ZERO,
        )
    }

    @Transactional(readOnly = true)
    override fun getSpfContributionClaimsFor(reportId: Long): List<ProjectReportSpfContributionClaim> =
        spfContributionClaimRepository.getAllByReportEntityIdIn(setOf(reportId)).map { it.toModel() }

    @Transactional
    override fun updateContributionClaimReportedAmount(
        reportId: Long,
        toUpdate: Map<Long, BigDecimal>
    ): List<ProjectReportSpfContributionClaim> {
        val contributions = spfContributionClaimRepository.getAllByReportEntityIdIn(setOf(reportId))
        contributions.updateWith(toUpdate)
        return contributions.map { it.toModel() }
    }

    @Transactional(readOnly = true)
    override fun getSpfContributionCumulative(reportIds: Set<Long>): SpfPreviouslyReportedByContributionSource {
        val previouslyReportedByContributionSource = spfContributionClaimRepository.getPreviouslyReportedContributionAmount(reportIds)

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

    @Transactional(readOnly = true)
    override fun getPreviouslyReportedSpfContributions(reportIds: Set<Long>): ReportExpenditureCoFinancingColumn {
        val allContributions = spfContributionClaimRepository.getAllByReportEntityIdIn(reportIds)
            .groupBy { it.programmeFund?.id }

        val partnerContributions = (allContributions[null] ?: emptyList()).groupBy { it.legalStatus!! }
        val funds = allContributions.mapValues { it.value.getCurrentSum() }

        return ReportExpenditureCoFinancingColumn(
            funds = funds,
            partnerContribution = allContributions[null].getCurrentSum(),
            publicContribution = partnerContributions[ProjectPartnerContributionStatus.Public].getCurrentSum(),
            automaticPublicContribution = partnerContributions[ProjectPartnerContributionStatus.AutomaticPublic].getCurrentSum(),
            privateContribution = partnerContributions[ProjectPartnerContributionStatus.Private].getCurrentSum(),
            sum = funds.values.sumOf { it },
        )
    }

    @Transactional(readOnly = true)
    override fun getCurrentSpfContribution(reportId: Long): ReportCertificateCoFinancingColumn {
        val split = getCurrentSpfContributionSplit(reportId)
        if (split != null)
            return ReportCertificateCoFinancingColumn(
                funds = split.fundsSorted.toMap().mapKeys { it.key.id }.plus(null to split.partnerContribution),
                partnerContribution = split.partnerContribution,
                publicContribution = split.publicContribution,
                automaticPublicContribution = split.automaticPublicContribution,
                privateContribution = split.privateContribution,
                sum = split.total,
            )
        else
            return emptySplit
    }

    @Transactional(readOnly = true)
    override fun getCurrentSpfContributionSplit(reportId: Long): FinancingSourceBreakdownLine? {
        val allContributions = spfContributionClaimRepository.getAllByReportEntityIdIn(setOf(reportId))
            .groupBy { it.programmeFund }

        if (allContributions.isEmpty())
            return null

        val partnerContributions = (allContributions[null] ?: emptyList()).groupBy { it.legalStatus!! }
        val funds = allContributions.filter { it.key != null }.map { Pair(it.key!!.toModel(), it.value.getCurrentSum()) }

        return FinancingSourceBreakdownLine(
            partnerReportId = null,
            partnerReportNumber = null,
            spfLine = true,
            partnerId = null,
            partnerRole = null,
            partnerNumber = null,
            fundsSorted = funds,
            partnerContribution = allContributions[null].getCurrentSum(),
            publicContribution = partnerContributions[ProjectPartnerContributionStatus.Public].getCurrentSum(),
            automaticPublicContribution = partnerContributions[ProjectPartnerContributionStatus.AutomaticPublic].getCurrentSum(),
            privateContribution = partnerContributions[ProjectPartnerContributionStatus.Private].getCurrentSum(),
            total = funds.sumOf { it.second }.plus(allContributions[null].getCurrentSum()),
            split = emptyList(),
        )
    }

    @Transactional(readOnly = true)
    override fun getCurrentSpfContributions(reportIds: Set<Long>): Map<Long, BigDecimal> =
        spfContributionClaimRepository.getCurrentPerReport(reportIds).toMap()

    @Transactional
    override fun resetSpfContributionClaims(reportId: Long) {
        spfContributionClaimRepository.getAllByReportEntityIdIn(setOf(reportId)).forEach { contributionClaim ->
            contributionClaim.currentlyReported = BigDecimal.ZERO
        }
    }

    private fun List<ProjectReportSpfContributionClaimEntity>.updateWith(toUpdate: Map<Long, BigDecimal>) {
        this.forEach { contributionClaim ->
            contributionClaim.currentlyReported = toUpdate[contributionClaim.id] ?: contributionClaim.currentlyReported
        }
    }

    private fun List<ProjectReportSpfContributionClaimEntity>?.getCurrentSum() =
        this?.sumOf { it.currentlyReported } ?: BigDecimal.ZERO

}
