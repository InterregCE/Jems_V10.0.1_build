package io.cloudflight.jems.server.project.controller.budget

import io.cloudflight.jems.api.project.dto.budget.ProjectPartnerBudgetPerPeriodDTO
import io.cloudflight.jems.api.project.dto.budget.ProjectPeriodBudgetDTO
import io.cloudflight.jems.server.project.controller.partner.toDto
import io.cloudflight.jems.server.project.service.model.ProjectPartnerBudgetPerPeriod
import io.cloudflight.jems.server.project.service.model.ProjectPeriodBudget

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
    isLastPeriod = this.isLastPeriod
)
