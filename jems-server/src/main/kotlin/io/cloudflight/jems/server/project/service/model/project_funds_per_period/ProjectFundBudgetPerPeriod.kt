package io.cloudflight.jems.server.project.service.model.project_funds_per_period

import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import io.cloudflight.jems.server.project.service.model.ProjectPartnerCostType
import java.math.BigDecimal

data class ProjectFundBudgetPerPeriod(
    val fund: ProgrammeFund,
    val costType: ProjectPartnerCostType,
    val periodFunds: MutableList<ProjectPeriodFund>,
    val totalFundBudget: BigDecimal
)
