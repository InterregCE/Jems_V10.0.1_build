package io.cloudflight.jems.api.project.dto.budget

import java.math.BigDecimal

data class ProjectBudgetOverviewPerPartnerPerPeriodDTO(
    val partnersBudgetPerPeriod: List<ProjectPartnerBudgetPerPeriodDTO>,
    val totals: List<BigDecimal>,
    val totalsPercentage: List<BigDecimal>
)
