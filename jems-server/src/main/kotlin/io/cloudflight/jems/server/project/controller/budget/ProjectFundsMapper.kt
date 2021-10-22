package io.cloudflight.jems.server.project.controller.budget

import io.cloudflight.jems.api.project.dto.budget.ProjectPartnerFundsPerPeriodDTO
import io.cloudflight.jems.api.project.dto.budget.ProjectPeriodFundDTO
import io.cloudflight.jems.server.programme.controller.fund.toDto
import io.cloudflight.jems.server.project.service.model.project_funds_per_period.ProjectPartnerFundsPerPeriod
import io.cloudflight.jems.server.project.service.model.project_funds_per_period.ProjectPeriodFund

fun ProjectPartnerFundsPerPeriod.toDto() = ProjectPartnerFundsPerPeriodDTO(
    fund = this.fund.toDto(),
    periodFunds = this.periodFunds.map {it.toDto()}.toSet(),
    totalFundBudget = this.totalFundBudget
)

fun ProjectPeriodFund.toDto() = ProjectPeriodFundDTO(
    periodNumber = this.periodNumber,
    totalFundsPerPeriod = this.totalFundsPerPeriod
)
