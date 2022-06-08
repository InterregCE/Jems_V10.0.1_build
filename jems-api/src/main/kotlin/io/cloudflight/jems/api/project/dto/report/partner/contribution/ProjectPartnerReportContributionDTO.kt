package io.cloudflight.jems.api.project.dto.report.partner.contribution

import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerContributionStatusDTO
import io.cloudflight.jems.api.project.dto.report.file.ProjectReportFileMetadataDTO

data class ProjectPartnerReportContributionDTO(
    val id: Long,
    val sourceOfContribution: String?,
    val legalStatus: ProjectPartnerContributionStatusDTO?,
    val createdInThisReport: Boolean,
    val numbers: ProjectPartnerReportContributionRowDTO,
    val attachment: ProjectReportFileMetadataDTO?,
)
