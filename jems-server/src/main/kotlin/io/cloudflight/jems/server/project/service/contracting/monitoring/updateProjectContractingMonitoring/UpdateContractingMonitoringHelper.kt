package io.cloudflight.jems.server.project.service.contracting.monitoring.updateProjectContractingMonitoring

import io.cloudflight.jems.server.payments.model.regular.contributionMeta.ContributionMeta
import io.cloudflight.jems.server.project.service.lumpsum.model.ProjectLumpSum
import io.cloudflight.jems.server.project.service.lumpsum.model.ProjectPartnerLumpSum
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportCoFinancingBreakdown.DetailedSplit

fun Map<ProjectLumpSum, Map<ProjectPartnerLumpSum, List<DetailedSplit>>>.sumByFund(): Map<Int, Map<Long?, DetailedSplit>> =
    mapKeys { (lumpSum, _) -> lumpSum.orderNr }
        .mapValues { (_, partnersAndSplits) ->
            partnersAndSplits.values
                .flatten()
                .groupBy { it.fundIdOrPartnerContributionWhenNull }
                .mapValues { (_, sameFundLines) -> sameFundLines.sum() }
        }

private fun List<DetailedSplit>.sum() = reduce { f, s -> f.plus(s) }

private fun DetailedSplit.plus(other: DetailedSplit) = DetailedSplit(
    fundIdOrPartnerContributionWhenNull = fundIdOrPartnerContributionWhenNull,
    value = value.plus(other.value),
    ofWhichPublic = ofWhichPublic.plus(other.ofWhichPublic),
    ofWhichAutoPublic = ofWhichAutoPublic.plus(other.ofWhichAutoPublic),
    ofWhichPrivate = ofWhichPrivate.plus(other.ofWhichPrivate),
    partnerContribution = partnerContribution.plus(other.partnerContribution)
)

fun Map<ProjectLumpSum, Map<ProjectPartnerLumpSum, List<DetailedSplit>>>.onlyPartnerContributions(projectId: Long) =
    flatMap { (lumpSum, partners) ->
        partners.map { (partner, fundSplits) ->
            val partnerContribution = fundSplits.first { it.isPartnerContribution() }
            ContributionMeta(
                projectId = projectId,
                partnerId = partner.partnerId,
                programmeLumpSumId = lumpSum.programmeLumpSumId,
                orderNr = lumpSum.orderNr,
                partnerContribution = partnerContribution.value,
                publicContribution = partnerContribution.ofWhichPublic,
                automaticPublicContribution = partnerContribution.ofWhichAutoPublic,
                privateContribution = partnerContribution.ofWhichPrivate,
            )
        }
    }
