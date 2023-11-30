package io.cloudflight.jems.api.project.dto.report.project.spfContribution

import java.math.BigDecimal

data class ProjectReportSpfFundingSourceTotalDTO(
    val amountInAf: BigDecimal,
    val previouslyReported: BigDecimal,
    val currentlyReported: BigDecimal,
    val totalReportedSoFar: BigDecimal
)
