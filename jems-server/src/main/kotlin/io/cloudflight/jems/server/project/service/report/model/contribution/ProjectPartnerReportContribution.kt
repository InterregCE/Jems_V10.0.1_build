package io.cloudflight.jems.server.project.service.report.model.contribution

import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerContributionStatus

data class ProjectPartnerReportContribution(
    val id: Long,
    val sourceOfContribution: String?,
    val legalStatus: ProjectPartnerContributionStatus?,
    val createdInThisReport: Boolean,
    val numbers: ProjectPartnerReportContributionRow,
)
