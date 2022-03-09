package io.cloudflight.jems.api.project.dto.report.partner.contribution

import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerContributionStatusDTO
import java.math.BigDecimal

data class UpdateProjectPartnerReportContributionCustomDTO(
    val sourceOfContribution: String,
    val legalStatus: ProjectPartnerContributionStatusDTO,
    val currentlyReported: BigDecimal,
)
