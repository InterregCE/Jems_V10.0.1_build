package io.cloudflight.jems.server.project.repository.partner.budget.mappers

import io.cloudflight.jems.server.common.entity.extractField
import io.cloudflight.jems.server.project.entity.partner.budget.general.ProjectPartnerBudgetGeneralBase
import io.cloudflight.jems.server.project.entity.partner.budget.general.ProjectPartnerBudgetGeneralRow
import io.cloudflight.jems.server.project.service.partner.model.BudgetGeneralCostEntry
import io.cloudflight.jems.server.project.service.partner.model.BudgetPeriod

fun List<ProjectPartnerBudgetGeneralRow>.toBudgetGeneralCostEntryList() =
    this.groupBy { it.getId() }.map { groupedRows ->
        BudgetGeneralCostEntry(
            id = groupedRows.key,
            description = groupedRows.value.extractField { it.getDescription() },
            comments = groupedRows.value.extractField { it.getComments() },
            awardProcedures = groupedRows.value.extractField { it.getAwardProcedures() },
            unitType = groupedRows.value.extractField { it.getUnitType() },
            budgetPeriods = groupedRows.value.filter { it.getPeriodNumber() != null }
                .mapTo(HashSet()) { BudgetPeriod(it.getPeriodNumber()!!, it.getAmount()) },
            unitCostId = groupedRows.value.first().getUnitCostId(),
            investmentId = groupedRows.value.first().getInvestmentId(),
            numberOfUnits = groupedRows.value.first().getNumberOfUnits(),
            pricePerUnit = groupedRows.value.first().getPricePerUnit(),
            rowSum = groupedRows.value.first().getRowSum()
        )
    }

fun ProjectPartnerBudgetGeneralBase.toBudgetGeneralCostEntry() = BudgetGeneralCostEntry(
    id = id,
    description = translatedValues.extractField { it.description },
    unitType = translatedValues.extractField { it.unitType },
    awardProcedures = translatedValues.extractField { it.awardProcedures },
    budgetPeriods = budgetPeriodEntities.map { BudgetPeriod(it.budgetPeriodId.period.id.number, it.amount) }
        .toMutableSet(),
    investmentId = investmentId,
    unitCostId = unitCostId,
    numberOfUnits = baseProperties.numberOfUnits,
    pricePerUnit = pricePerUnit,
    rowSum = baseProperties.rowSum,
    comments = translatedValues.extractField { it.comments },
)
