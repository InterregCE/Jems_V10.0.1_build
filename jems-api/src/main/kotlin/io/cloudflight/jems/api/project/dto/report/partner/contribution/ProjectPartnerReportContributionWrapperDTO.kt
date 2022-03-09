package io.cloudflight.jems.api.project.dto.report.partner.contribution

data class ProjectPartnerReportContributionWrapperDTO(
    val contributions: List<ProjectPartnerReportContributionDTO>,
    val overview: ProjectPartnerReportContributionOverviewDTO,
)
