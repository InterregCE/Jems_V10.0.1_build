package io.cloudflight.jems.server.project.service.model

import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerSummary
import java.math.BigDecimal

data class ProjectPartnerBudgetPerFund(
    val partner: ProjectPartnerSummary? = null,
    val budgetPerFund: Set<PartnerBudgetPerFund> = emptySet(),
    val publicContribution: BigDecimal? = BigDecimal.ZERO,
    val autoPublicContribution: BigDecimal? = BigDecimal.ZERO,
    val privateContribution: BigDecimal? = BigDecimal.ZERO,
    val totalPartnerContribution: BigDecimal? = BigDecimal.ZERO,
    val totalEligibleBudget: BigDecimal? = BigDecimal.ZERO,
    val percentageOfTotalEligibleBudget: BigDecimal? = BigDecimal.ZERO
)
