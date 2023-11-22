package io.cloudflight.jems.server.project.service.report.model.project.spfContributionClaim

import java.math.BigDecimal

data class SpfPreviouslyReportedContributionRow(
    val id: Long,
    val programmeFundId: Long?,
    val applicationFormPartnerContributionId: Long?,
    val previouslyReportedAmount: BigDecimal
)
