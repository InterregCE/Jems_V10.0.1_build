package io.cloudflight.jems.api.project.dto.report.project.spfContribution

import io.cloudflight.jems.api.programme.dto.fund.ProgrammeFundDTO
import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerContributionStatusDTO
import java.math.BigDecimal

data class ProjectReportSpfContributionClaimDTO(
    val id: Long,
    val reportId: Long,
    val programmeFund: ProgrammeFundDTO?,
    val sourceOfContribution: String?,
    val legalStatus: ProjectPartnerContributionStatusDTO?,
    val amountInAf: BigDecimal,
    val previouslyReported: BigDecimal,
    val currentlyReported: BigDecimal,
    val totalReportedSoFar: BigDecimal
)
