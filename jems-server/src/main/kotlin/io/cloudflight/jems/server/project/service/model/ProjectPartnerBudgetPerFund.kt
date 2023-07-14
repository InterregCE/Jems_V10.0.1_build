package io.cloudflight.jems.server.project.service.model

import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerSummary
import java.math.BigDecimal

data class ProjectPartnerBudgetPerFund(
    val partner: ProjectPartnerSummary? = null,
    val costType: ProjectPartnerCostType? = null,
    val budgetPerFund: Set<PartnerBudgetPerFund> = emptySet(),
    val publicContribution: BigDecimal,
    val autoPublicContribution: BigDecimal,
    val privateContribution: BigDecimal,
    val totalPartnerContribution: BigDecimal,
    val totalEligibleBudget: BigDecimal,
    val percentageOfTotalEligibleBudget: BigDecimal? = BigDecimal.ZERO
)
