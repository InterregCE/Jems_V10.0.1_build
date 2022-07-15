package io.cloudflight.jems.server.project.service.report.partner.contribution

import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerContributionStatus
import io.cloudflight.jems.server.project.service.report.model.contribution.ProjectPartnerReportContribution
import io.cloudflight.jems.server.project.service.report.model.contribution.ProjectPartnerReportContributionData
import io.cloudflight.jems.server.project.service.report.model.contribution.ProjectPartnerReportContributionOverview
import io.cloudflight.jems.server.project.service.report.model.contribution.ProjectPartnerReportContributionRow
import io.cloudflight.jems.server.project.service.report.model.contribution.withoutCalculations.ProjectPartnerReportEntityContribution
import java.math.BigDecimal

private val emptySumUp = ProjectPartnerReportContributionRow(
    amount = BigDecimal.ZERO,
    previouslyReported = BigDecimal.ZERO,
    currentlyReported = BigDecimal.ZERO,
    totalReportedSoFar = BigDecimal.ZERO,
)

fun List<ProjectPartnerReportEntityContribution>.toModelData() = ProjectPartnerReportContributionData(
    contributions = toCalculatedModel(),
    overview = extractOverview(),
)

private fun List<ProjectPartnerReportEntityContribution>.toCalculatedModel(): List<ProjectPartnerReportContribution> {
    return map {
        ProjectPartnerReportContribution(
            id = it.id,
            sourceOfContribution = it.sourceOfContribution,
            legalStatus = it.legalStatus,
            createdInThisReport = it.createdInThisReport,
            numbers = ProjectPartnerReportContributionRow(
                amount = it.amount,
                previouslyReported = it.previouslyReported,
                currentlyReported = it.currentlyReported,
                totalReportedSoFar = it.previouslyReported.plus(it.currentlyReported),
            ),
            attachment = it.attachment,
        )
    }
}

fun List<ProjectPartnerReportEntityContribution>.extractOverview(): ProjectPartnerReportContributionOverview {
    val byLegalStatus = groupBy { it.legalStatus }

    return ProjectPartnerReportContributionOverview(
        public = byLegalStatus[ProjectPartnerContributionStatus.Public].sumUp(),
        automaticPublic = byLegalStatus[ProjectPartnerContributionStatus.AutomaticPublic].sumUp(),
        private = byLegalStatus[ProjectPartnerContributionStatus.Private].sumUp(),
        total = emptySumUp,
    ).apply {
        total = public.plus(automaticPublic).plus(private)
    }
}

private fun List<ProjectPartnerReportEntityContribution>?.sumUp(): ProjectPartnerReportContributionRow {
    if (this == null)
        return emptySumUp
    return this.fold(emptySumUp) { first, second ->
        ProjectPartnerReportContributionRow(
            amount = first.amount.plus(second.amount),
            previouslyReported = first.previouslyReported.plus(second.previouslyReported),
            currentlyReported = first.currentlyReported.plus(second.currentlyReported),
            totalReportedSoFar = first.totalReportedSoFar
                .plus(second.previouslyReported)
                .plus(second.currentlyReported),
        )
    }
}

private fun ProjectPartnerReportContributionRow.plus(other: ProjectPartnerReportContributionRow) =
    ProjectPartnerReportContributionRow(
        amount = this.amount.plus(other.amount),
        previouslyReported = this.previouslyReported.plus(other.previouslyReported),
        currentlyReported = this.currentlyReported.plus(other.currentlyReported),
        totalReportedSoFar = this.totalReportedSoFar.plus(other.totalReportedSoFar),
    )
