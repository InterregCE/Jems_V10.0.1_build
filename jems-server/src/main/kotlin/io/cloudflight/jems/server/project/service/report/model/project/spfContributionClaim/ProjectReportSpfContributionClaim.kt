package io.cloudflight.jems.server.project.service.report.model.project.spfContributionClaim

import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerContributionStatus
import java.math.BigDecimal

data class ProjectReportSpfContributionClaim(
    val id: Long,
    val reportId: Long,

    val programmeFund: ProgrammeFund?,

    val idFromApplicationForm: Long?,
    val sourceOfContribution: String?,
    val legalStatus: ProjectPartnerContributionStatus?,

    val amountInAf: BigDecimal,
    val previouslyReported: BigDecimal,
    val currentlyReported: BigDecimal,
    var totalReportedSoFar: BigDecimal = BigDecimal.ZERO
)
