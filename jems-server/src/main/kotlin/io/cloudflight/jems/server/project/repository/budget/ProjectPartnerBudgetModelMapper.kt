package io.cloudflight.jems.server.project.repository.budget

import io.cloudflight.jems.server.project.entity.partner.budget.ProjectPartnerBudgetRow
import io.cloudflight.jems.server.project.entity.partner.budget.ProjectPartnerBudgetView
import io.cloudflight.jems.server.project.service.budget.model.ProjectPartnerCost
import io.cloudflight.jems.server.project.service.budget.model.ProjectSpfBudgetPerPeriod
import io.cloudflight.jems.server.project.service.model.BudgetCostsDetail
import io.cloudflight.jems.server.project.service.model.ProjectPeriod
import io.cloudflight.jems.server.project.service.model.ProjectPeriodBudget
import io.cloudflight.jems.server.project.service.partner.model.PartnerTotalBudgetPerCostCategory
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerTotalBudgetEntry
import java.math.BigDecimal

fun ProjectPartnerBudgetView.toProjectPartnerBudget() = ProjectPartnerCost(
    partnerId = partnerId,
    sum = sum
)

fun List<ProjectPartnerBudgetView>.toProjectPartnerBudget() = map { it.toProjectPartnerBudget() }

fun ProjectPartnerBudgetRow.toProjectPartnerBudget() = ProjectPartnerCost(
    partnerId = partnerId,
    sum = sum
)

fun List<ProjectPartnerBudgetRow>.toProjectPartnerBudgetHistoricalData() = map { it.toProjectPartnerBudget() }

fun ProjectPartnerTotalBudgetEntry.toModel() = PartnerTotalBudgetPerCostCategory(
    partnerId,
    officeAndAdministrationOnStaffCostsFlatRate,
    officeAndAdministrationOnDirectCostsFlatRate,
    travelAndAccommodationOnStaffCostsFlatRate,
    staffCostsFlatRate,
    otherCostsOnStaffCostsFlatRate,
    unitCostTotal ?: BigDecimal.ZERO,
    equipmentCostTotal ?: BigDecimal.ZERO,
    externalCostTotal ?: BigDecimal.ZERO,
    infrastructureCostTotal ?: BigDecimal.ZERO,
    travelCostTotal ?: BigDecimal.ZERO,
    staffCostTotal ?: BigDecimal.ZERO,
    lumpSumsTotal ?: BigDecimal.ZERO
)

fun ProjectSpfBudgetPerPeriod.toProjectPeriodBudget(projectPeriods: List<ProjectPeriod>) =
    ProjectPeriodBudget(
        periodNumber = periodNumber,
        periodStart = projectPeriods.first { it.number == periodNumber }.start,
        periodEnd = projectPeriods.first { it.number == periodNumber }.end,
        totalBudgetPerPeriod = spfCostPerPeriod,
        budgetPerPeriodDetail = BudgetCostsDetail(),
        lastPeriod = false
    )
