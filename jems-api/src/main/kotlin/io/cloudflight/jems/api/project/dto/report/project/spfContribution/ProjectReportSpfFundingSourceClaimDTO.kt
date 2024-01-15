package io.cloudflight.jems.api.project.dto.report.project.spfContribution

data class ProjectReportSpfFundingSourceClaimDTO(
    val coFinancingSources: List<ProjectReportSpfCoFinancingClaimDTO>,
    val partnerContributionSources: List<ProjectReportSpfContributionClaimDTO>,
    val total: ProjectReportSpfFundingSourceTotalDTO
)



