package io.cloudflight.jems.server.project.service.report.model.project.spfContributionClaim

import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerContributionStatus
import java.math.BigDecimal

data class SpfPreviouslyReportedContributionRow(
    val id: Long,
    val programmeFundId: Long?,
    val applicationFormPartnerContributionId: Long?,
    val sourceOfContribution: String?,
    val legalStatus: ProjectPartnerContributionStatus?,
    val previouslyReportedAmount: BigDecimal
)
