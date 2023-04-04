package io.cloudflight.jems.server.project.service.report.model.partner.contribution

import io.cloudflight.jems.server.common.file.service.model.JemsFileMetadata
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerContributionStatus

data class ProjectPartnerReportContribution(
    val id: Long,
    val sourceOfContribution: String?,
    val legalStatus: ProjectPartnerContributionStatus?,
    val createdInThisReport: Boolean,
    val numbers: ProjectPartnerReportContributionRow,
    val attachment: JemsFileMetadata?,
)
