package io.cloudflight.jems.api.project.dto.budget

import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerSummaryDTO
import java.math.BigDecimal

data class ProjectPartnerBudgetPerFundDTO(
    val partner: ProjectPartnerSummaryDTO?,
    val budgetPerFund: Set<PartnerBudgetPerFundDTO> = emptySet(),
    val publicContribution: BigDecimal?,
    val autoPublicContribution: BigDecimal?,
    val privateContribution: BigDecimal?,
    val totalPartnerContribution: BigDecimal?,
    val totalEligibleBudget: BigDecimal?,
    val percentageOfTotalEligibleBudget: BigDecimal?
)
