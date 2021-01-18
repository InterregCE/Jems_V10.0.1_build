package io.cloudflight.jems.server.project.repository.partner.budget

import io.cloudflight.jems.server.programme.entity.costoption.ProgrammeUnitCostEntity
import io.cloudflight.jems.server.project.entity.partner.budget.ProjectPartnerBudgetUnitCostEntity
import io.cloudflight.jems.server.project.service.partner.model.BudgetUnitCostEntry

fun List<BudgetUnitCostEntry>.toEntity(
    partnerId: Long,
    getProgrammeUnitCost: (Long) -> ProgrammeUnitCostEntity
) = map {
    ProjectPartnerBudgetUnitCostEntity(
        id = it.id ?: 0,
        partnerId = partnerId,
        numberOfUnits = it.numberOfUnits,
        unitCost = getProgrammeUnitCost.invoke(it.unitCostId),
        rowSum = it.rowSum
    )
}

fun Iterable<ProjectPartnerBudgetUnitCostEntity>.toModel() = map {
    BudgetUnitCostEntry(
        id = it.id,
        numberOfUnits = it.numberOfUnits,
        unitCostId = it.unitCost.id,
        rowSum = it.rowSum
    )
}
