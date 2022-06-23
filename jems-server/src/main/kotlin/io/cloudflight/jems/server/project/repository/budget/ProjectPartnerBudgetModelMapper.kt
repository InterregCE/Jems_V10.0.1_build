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

fun ProjectPeriod.toProjectPeriodBudget(
    spfCostPerPeriod: List<ProjectSpfBudgetPerPeriod>,
    spfTotalBudget: BigDecimal,
    totalBudgetPerPeriods: BigDecimal,
    maxPeriod: ProjectPeriod?
) =
    ProjectPeriodBudget(
        periodNumber = number,
        periodStart = start,
        periodEnd = end,
        totalBudgetPerPeriod =
        if (spfCostPerPeriod.firstOrNull {it.periodNumber == number} == null) {
            if (maxPeriod != null && number == maxPeriod.number)
                spfTotalBudget - totalBudgetPerPeriods
            else
                BigDecimal.ZERO
        }
        else {
            if (maxPeriod != null && number == maxPeriod.number)
                spfCostPerPeriod.first {it.periodNumber == number}.spfCostPerPeriod + (spfTotalBudget - totalBudgetPerPeriods)
            else
                spfCostPerPeriod.first {it.periodNumber == number}.spfCostPerPeriod
        },
        budgetPerPeriodDetail = BudgetCostsDetail(),
        lastPeriod = false
    )
