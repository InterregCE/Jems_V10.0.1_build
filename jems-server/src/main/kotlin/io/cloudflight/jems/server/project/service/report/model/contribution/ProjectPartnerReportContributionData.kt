package io.cloudflight.jems.server.project.service.report.model.contribution

data class ProjectPartnerReportContributionData(
    val contributions: List<ProjectPartnerReportContribution>,
    val overview: ProjectPartnerReportContributionOverview,
)
