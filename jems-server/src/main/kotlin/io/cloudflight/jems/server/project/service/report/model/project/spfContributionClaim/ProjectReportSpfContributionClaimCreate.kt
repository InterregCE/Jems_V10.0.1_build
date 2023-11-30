package io.cloudflight.jems.server.project.service.report.model.project.spfContributionClaim

import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerContributionStatus
import java.math.BigDecimal

data class ProjectReportSpfContributionClaimCreate(
    val fundId: Long?,
    val idFromApplicationForm: Long?,
    val sourceOfContribution: String?,
    val legalStatus: ProjectPartnerContributionStatus?,
    val amountInAf: BigDecimal,
    val previouslyReported: BigDecimal,
    val currentlyReported: BigDecimal,
)
