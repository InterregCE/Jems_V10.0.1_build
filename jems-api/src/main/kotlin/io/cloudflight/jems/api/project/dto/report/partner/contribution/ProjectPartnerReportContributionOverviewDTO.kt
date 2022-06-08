package io.cloudflight.jems.api.project.dto.report.partner.contribution

data class ProjectPartnerReportContributionOverviewDTO(
    val publicContribution: ProjectPartnerReportContributionRowDTO,
    val automaticPublicContribution: ProjectPartnerReportContributionRowDTO,
    val privateContribution: ProjectPartnerReportContributionRowDTO,
    val total: ProjectPartnerReportContributionRowDTO,
)
