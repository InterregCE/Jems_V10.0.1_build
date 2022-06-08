package io.cloudflight.jems.server.project.service.report.model.contribution

import java.math.BigDecimal

data class ProjectPartnerReportContributionRow(
    val amount: BigDecimal,
    val previouslyReported: BigDecimal,
    val currentlyReported: BigDecimal,
    val totalReportedSoFar: BigDecimal,
)
