package io.cloudflight.jems.server.project.controller.report.project.spfContributionClaim

import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerContributionStatusDTO
import io.cloudflight.jems.api.project.dto.report.project.spfContribution.ProjectReportSpfContributionClaimDTO
import io.cloudflight.jems.api.project.dto.report.project.spfContribution.ProjectReportSpfContributionClaimUpdateDTO
import io.cloudflight.jems.server.programme.controller.fund.toDto
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerContributionStatus
import io.cloudflight.jems.server.project.service.report.model.project.spfContributionClaim.ProjectReportSpfContributionClaim
import io.cloudflight.jems.server.project.service.report.model.project.spfContributionClaim.ProjectReportSpfContributionClaimUpdate


fun ProjectReportSpfContributionClaim.toDto() = ProjectReportSpfContributionClaimDTO(
    id = id,
    reportId = reportId,
    programmeFund = programmeFund?.toDto(),
    sourceOfContribution = sourceOfContribution,
    legalStatus = legalStatus.toDto(),
    amountInAf = amountInAf,
    previouslyReported = previouslyReported,
    currentlyReported = currentlyReported,
    totalReportedSoFar = totalReportedSoFar
)

fun List<ProjectReportSpfContributionClaim>.toDtoList() = this.map { it.toDto() }

fun List<ProjectReportSpfContributionClaimUpdateDTO>.toModelUpdateList() = this.map { it.toModel() }
fun ProjectReportSpfContributionClaimUpdateDTO.toModel() = ProjectReportSpfContributionClaimUpdate(
    id = id,
    currentlyReported = currentlyReported
)

fun ProjectPartnerContributionStatus?.toDto(): ProjectPartnerContributionStatusDTO? =
    if(this != null) ProjectPartnerContributionStatusDTO.valueOf(this.name) else null

