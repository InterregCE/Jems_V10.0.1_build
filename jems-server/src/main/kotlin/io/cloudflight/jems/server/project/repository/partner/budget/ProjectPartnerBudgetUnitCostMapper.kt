package io.cloudflight.jems.server.project.repository.partner.budget

import io.cloudflight.jems.server.programme.entity.costoption.ProgrammeUnitCostEntity
import io.cloudflight.jems.server.project.entity.partner.budget.ProjectPartnerBudgetUnitCostEntity
import io.cloudflight.jems.server.project.service.partner.model.BudgetUnitCostEntry

fun BudgetUnitCostEntry.toEntity(
    partnerId: Long,
    getProgrammeUnitCost: (Long) -> ProgrammeUnitCostEntity
) = ProjectPartnerBudgetUnitCostEntity(
    id = id ?: 0,
    partnerId = partnerId,
    numberOfUnits = numberOfUnits,
    unitCost = unitCostId?.let { getProgrammeUnitCost.invoke(it) },
    rowSum = rowSum
)

fun ProjectPartnerBudgetUnitCostEntity.toProjectPartnerBudgetUnitCost() = BudgetUnitCostEntry(
    id = id,
    numberOfUnits = numberOfUnits,
    unitCostId = unitCost?.id,
    rowSum = rowSum
)
