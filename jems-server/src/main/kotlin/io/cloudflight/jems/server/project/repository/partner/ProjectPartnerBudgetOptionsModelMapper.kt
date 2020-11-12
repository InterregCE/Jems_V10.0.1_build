package io.cloudflight.jems.server.project.repository.partner

import io.cloudflight.jems.server.project.entity.partner.budget.ProjectPartnerBudgetOptionsEntity
import io.cloudflight.jems.server.project.service.budget.model.ProjectPartnerBudgetOptions

fun ProjectPartnerBudgetOptionsEntity.toProjectPartnerBudgetOptions() = ProjectPartnerBudgetOptions(
    partnerId = partnerId,
    officeAdministrationFlatRate = officeAdministrationFlatRate,
    staffCostsFlatRate = staffCostsFlatRate
)

fun Iterable<ProjectPartnerBudgetOptionsEntity>.toProjectPartnerBudgetOptions() = map { it.toProjectPartnerBudgetOptions() }
