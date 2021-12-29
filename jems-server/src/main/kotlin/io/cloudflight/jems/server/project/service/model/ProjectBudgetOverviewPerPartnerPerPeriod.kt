package io.cloudflight.jems.server.project.service.model

import java.math.BigDecimal

data class ProjectBudgetOverviewPerPartnerPerPeriod(
    val partnersBudgetPerPeriod: List<ProjectPartnerBudgetPerPeriod>,
    val totals: List<BigDecimal>,
    val totalsPercentage: List<BigDecimal>
)
