package io.cloudflight.jems.api.project.dto.report.project.spfContribution

import java.math.BigDecimal

data class ProjectReportSpfContributionClaimUpdateDTO(
    val id: Long,
    val currentlyReported: BigDecimal
)
