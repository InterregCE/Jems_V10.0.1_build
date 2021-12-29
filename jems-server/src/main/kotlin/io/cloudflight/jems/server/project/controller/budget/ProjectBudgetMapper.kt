package io.cloudflight.jems.server.project.controller.budget

import io.cloudflight.jems.api.project.dto.budget.PartnerBudgetPerFundDTO
import io.cloudflight.jems.api.project.dto.budget.ProjectBudgetOverviewPerPartnerPerPeriodDTO
import io.cloudflight.jems.api.project.dto.budget.ProjectPartnerBudgetPerFundDTO
import io.cloudflight.jems.api.project.dto.budget.ProjectPartnerBudgetPerPeriodDTO
import io.cloudflight.jems.api.project.dto.budget.ProjectPeriodBudgetDTO
import io.cloudflight.jems.server.programme.controller.fund.toDto
import io.cloudflight.jems.server.project.controller.partner.toDto
import io.cloudflight.jems.server.project.service.model.PartnerBudgetPerFund
import io.cloudflight.jems.server.project.service.model.ProjectBudgetOverviewPerPartnerPerPeriod
import io.cloudflight.jems.server.project.service.model.ProjectPartnerBudgetPerFund
import io.cloudflight.jems.server.project.service.model.ProjectPartnerBudgetPerPeriod
import io.cloudflight.jems.server.project.service.model.ProjectPeriodBudget

fun ProjectBudgetOverviewPerPartnerPerPeriod.toDto() = ProjectBudgetOverviewPerPartnerPerPeriodDTO(
    partnersBudgetPerPeriod.map { it.toDto() },
    totals,
    totalsPercentage
)

fun ProjectPartnerBudgetPerPeriod.toDto() = ProjectPartnerBudgetPerPeriodDTO(
    partner = this.partner.toDto(),
    periodBudgets = this.periodBudgets.map {it.toDto()}.toSet(),
    totalPartnerBudget = this.totalPartnerBudget
)

fun ProjectPeriodBudget.toDto() = ProjectPeriodBudgetDTO(
    periodNumber = this.periodNumber,
    periodStart = this.periodStart,
    periodEnd = this.periodEnd,
    totalBudgetPerPeriod = this.totalBudgetPerPeriod,
    isLastPeriod = this.lastPeriod
)

fun ProjectPartnerBudgetPerFund.toDto() = ProjectPartnerBudgetPerFundDTO(
    partner = this.partner?.toDto(),
    budgetPerFund = this.budgetPerFund.map { it.toDto() }.toSet(),
    publicContribution = this.publicContribution,
    autoPublicContribution = this.autoPublicContribution,
    privateContribution = this.privateContribution,
    totalPartnerContribution = this.totalPartnerContribution,
    totalEligibleBudget = this.totalEligibleBudget,
    percentageOfTotalEligibleBudget = this.percentageOfTotalEligibleBudget
)

fun PartnerBudgetPerFund.toDto() = PartnerBudgetPerFundDTO(
    fund = this.fund?.toDto(),
    percentage = this.percentage,
    percentageOfTotal = this.percentageOfTotal,
    value = this.value
)
