package io.cloudflight.jems.server.project.controller.budget

import io.cloudflight.jems.api.project.dto.budget.ProjectFundsPerPeriodDTO
import io.cloudflight.jems.api.project.dto.budget.ProjectFundBudgetPerPeriodDTO
import io.cloudflight.jems.api.project.dto.budget.ProjectPeriodFundDTO
import io.cloudflight.jems.server.programme.controller.fund.toDto
import io.cloudflight.jems.server.project.service.model.project_funds_per_period.ProjectFundsPerPeriod
import io.cloudflight.jems.server.project.service.model.project_funds_per_period.ProjectFundBudgetPerPeriod
import io.cloudflight.jems.server.project.service.model.project_funds_per_period.ProjectPeriodFund

fun ProjectFundBudgetPerPeriod.toDto() = ProjectFundBudgetPerPeriodDTO(
    fund = this.fund.toDto(),
    costType = costType.toDto(),
    periodFunds = this.periodFunds.map {it.toDto()}.toSet(),
    totalFundBudget = this.totalFundBudget
)

fun ProjectPeriodFund.toDto() = ProjectPeriodFundDTO(
    periodNumber = this.periodNumber,
    totalFundsPerPeriod = this.totalFundsPerPeriod
)

fun ProjectFundsPerPeriod.toDto() = ProjectFundsPerPeriodDTO (
    managementFundsPerPeriod = managementFundsPerPeriod.map { it.toDto() }.toSet(),
    spfFundsPerPeriod = spfFundsPerPeriod.map { it.toDto() }.toSet()
)
