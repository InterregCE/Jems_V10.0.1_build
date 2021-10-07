package io.cloudflight.jems.server.project.service.partner.model

import java.math.BigDecimal

data class ProjectBudgetPartnerSummary (
    val partnerSummary: ProjectPartnerSummary,
    val totalBudget: BigDecimal? = BigDecimal.ZERO
)
