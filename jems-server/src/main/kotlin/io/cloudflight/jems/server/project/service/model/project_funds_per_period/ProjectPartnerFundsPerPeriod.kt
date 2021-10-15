package io.cloudflight.jems.server.project.service.model.project_funds_per_period

import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import java.math.BigDecimal

data class ProjectPartnerFundsPerPeriod(
    val fund: ProgrammeFund,
    val periodFunds: MutableList<ProjectPeriodFund>,
    val totalFundBudget: BigDecimal
)
