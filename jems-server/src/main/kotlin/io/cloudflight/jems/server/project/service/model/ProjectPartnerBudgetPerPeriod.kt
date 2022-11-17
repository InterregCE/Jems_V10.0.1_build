package io.cloudflight.jems.server.project.service.model

import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerSummary
import java.math.BigDecimal

data class ProjectPartnerBudgetPerPeriod(
    val partner: ProjectPartnerSummary,
    val periodBudgets: MutableList<ProjectPeriodBudget>,
    val totalPartnerBudget: BigDecimal = BigDecimal.ZERO,
    val totalPartnerBudgetDetail: BudgetCostsDetail,
    val costType: ProjectPartnerCostType
)
