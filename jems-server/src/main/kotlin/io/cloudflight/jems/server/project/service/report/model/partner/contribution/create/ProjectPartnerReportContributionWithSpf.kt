package io.cloudflight.jems.server.project.service.report.model.partner.contribution.create

data class ProjectPartnerReportContributionWithSpf(
    val contributions: List<CreateProjectPartnerReportContribution>,
    val contributionsSpf: List<CreateProjectPartnerReportContribution>,
)
