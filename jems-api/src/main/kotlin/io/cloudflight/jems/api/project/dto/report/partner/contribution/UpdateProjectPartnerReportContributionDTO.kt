package io.cloudflight.jems.api.project.dto.report.partner.contribution

import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerContributionStatusDTO
import java.math.BigDecimal

data class UpdateProjectPartnerReportContributionDTO(
    val id: Long,
    val currentlyReported: BigDecimal,
    val sourceOfContribution: String? = null,
    val legalStatus: ProjectPartnerContributionStatusDTO? = null,
)
