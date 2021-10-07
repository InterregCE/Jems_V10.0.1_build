package io.cloudflight.jems.api.project.dto.partner

import java.math.BigDecimal

data class ProjectBudgetPartnerSummaryDTO (
    val partnerSummary: ProjectPartnerSummaryDTO,
    val totalBudget: BigDecimal? = BigDecimal.ZERO
)
