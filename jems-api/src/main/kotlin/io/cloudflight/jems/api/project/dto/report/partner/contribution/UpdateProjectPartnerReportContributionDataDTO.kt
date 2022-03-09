package io.cloudflight.jems.api.project.dto.report.partner.contribution

data class UpdateProjectPartnerReportContributionDataDTO(
    val toBeUpdated: Set<UpdateProjectPartnerReportContributionDTO>,
    val toBeDeletedIds: Set<Long>,
    val toBeCreated: List<UpdateProjectPartnerReportContributionCustomDTO>,
)
