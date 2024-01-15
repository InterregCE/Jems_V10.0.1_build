package io.cloudflight.jems.server.project.service.report.model.project.spfContributionClaim

import java.math.BigDecimal

data class ProjectReportSpfContributionClaimUpdate(
    val id: Long,
    val currentlyReported: BigDecimal,
)
