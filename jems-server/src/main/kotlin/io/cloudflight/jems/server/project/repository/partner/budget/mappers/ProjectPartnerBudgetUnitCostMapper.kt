package io.cloudflight.jems.server.project.repository.partner.budget.mappers

import io.cloudflight.jems.server.programme.entity.costoption.ProgrammeUnitCostEntity
import io.cloudflight.jems.server.project.entity.ProjectPeriodEntity
import io.cloudflight.jems.server.project.entity.partner.budget.BaseBudgetProperties
import io.cloudflight.jems.server.project.entity.partner.budget.BudgetPeriodId
import io.cloudflight.jems.server.project.entity.partner.budget.unit_cost.ProjectPartnerBudgetUnitCostEntity
import io.cloudflight.jems.server.project.entity.partner.budget.unit_cost.ProjectPartnerBudgetUnitCostPeriodEntity
import io.cloudflight.jems.server.project.entity.partner.budget.unit_cost.ProjectPartnerBudgetUnitCostRow
import io.cloudflight.jems.server.project.service.partner.model.BudgetPeriod
import io.cloudflight.jems.server.project.service.partner.model.BudgetUnitCostEntry

fun List<ProjectPartnerBudgetUnitCostRow>.toBudgetUnitCostEntryList() =
    this.groupBy { it.getId() }.map { groupedRows ->
        BudgetUnitCostEntry(
            id = groupedRows.key,
            budgetPeriods = groupedRows.value.filter { it.getPeriodNumber() != null }
                .mapTo(HashSet()) { BudgetPeriod(it.getPeriodNumber()!!, it.getAmount()) },
            unitCostId = groupedRows.value.first().getUnitCostId(),
            numberOfUnits = groupedRows.value.first().getNumberOfUnits(),
            rowSum = groupedRows.value.first().getRowSum()
        )
    }

fun List<BudgetUnitCostEntry>.toBudgetUnitCostEntities(
    partnerId: Long,
    getProgrammeUnitCost: (Long) -> ProgrammeUnitCostEntity,
    projectPeriodEntityReferenceResolver: (Int) -> ProjectPeriodEntity
) = map { it.toBudgetUnitCostEntity(partnerId, getProgrammeUnitCost, projectPeriodEntityReferenceResolver) }

fun BudgetUnitCostEntry.toBudgetUnitCostEntity(
    partnerId: Long,
    getProgrammeUnitCost: (Long) -> ProgrammeUnitCostEntity,
    projectPeriodEntityReferenceResolver: (Int) -> ProjectPeriodEntity
) =
    ProjectPartnerBudgetUnitCostEntity(
        id = id ?: 0,
        baseProperties = BaseBudgetProperties(partnerId, numberOfUnits, rowSum!!),
        unitCost = getProgrammeUnitCost.invoke(unitCostId),
        budgetPeriodEntities = mutableSetOf(),
    ).apply {
        budgetPeriodEntities.addAll(budgetPeriods.map {
            ProjectPartnerBudgetUnitCostPeriodEntity(
                BudgetPeriodId(
                    this,
                    projectPeriodEntityReferenceResolver.invoke(it.number)
                ),
                it.amount
            )
        }.toMutableSet())
    }


fun Iterable<ProjectPartnerBudgetUnitCostEntity>.toModel() = map {
    BudgetUnitCostEntry(
        id = it.id,
        numberOfUnits = it.baseProperties.numberOfUnits,
        budgetPeriods = it.budgetPeriodEntities.map { BudgetPeriod(it.budgetPeriodId.period.id.number, it.amount) }
            .toMutableSet(),
        unitCostId = it.unitCost.id,
        rowSum = it.baseProperties.rowSum
    )
}
