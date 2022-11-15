package io.cloudflight.jems.server.project.service.report.model.partner.contribution

data class ProjectPartnerReportContributionData(
    val contributions: List<ProjectPartnerReportContribution>,
    val overview: ProjectPartnerReportContributionOverview,
)
