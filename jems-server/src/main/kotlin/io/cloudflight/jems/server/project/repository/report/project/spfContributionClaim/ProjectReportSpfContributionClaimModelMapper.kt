package io.cloudflight.jems.server.project.repository.report.project.spfContributionClaim

import io.cloudflight.jems.server.programme.repository.fund.toModel
import io.cloudflight.jems.server.project.entity.report.project.ProjectReportSpfContributionClaimEntity
import io.cloudflight.jems.server.project.service.report.model.project.spfContributionClaim.ProjectReportSpfContributionClaim

fun ProjectReportSpfContributionClaimEntity.toModel() =  ProjectReportSpfContributionClaim(
    id = id,
    reportId = reportEntity.id,
    programmeFund = programmeFund?.toModel(),
    idFromApplicationForm = applicationFormPartnerContributionId,
    sourceOfContribution = sourceOfContribution,
    legalStatus = legalStatus,
    amountInAf = amountFromAf,
    previouslyReported = previouslyReported,
    currentlyReported = currentlyReported
)
