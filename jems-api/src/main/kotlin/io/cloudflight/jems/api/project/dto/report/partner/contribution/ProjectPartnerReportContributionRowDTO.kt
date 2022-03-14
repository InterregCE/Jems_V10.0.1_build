package io.cloudflight.jems.api.project.dto.report.partner.contribution

import java.math.BigDecimal

data class ProjectPartnerReportContributionRowDTO(
    val amount: BigDecimal,
    val previouslyReported: BigDecimal,
    val currentlyReported: BigDecimal,
    val totalReportedSoFar: BigDecimal,
)
