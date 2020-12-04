package io.cloudflight.jems.server.project.repository.partner.budget

import io.cloudflight.jems.server.project.entity.partner.budget.ProjectPartnerBudgetOptionsEntity
import io.cloudflight.jems.server.project.service.budget.model.ProjectPartnerBudgetOptions

fun ProjectPartnerBudgetOptions.toEntity() = ProjectPartnerBudgetOptionsEntity(
    partnerId = partnerId,
    officeAdministrationFlatRate = officeAndAdministrationFlatRate,
    travelAccommodationFlatRate = travelAndAccommodationFlatRate,
    staffCostsFlatRate = staffCostsFlatRate,
)

fun ProjectPartnerBudgetOptionsEntity.toModel() = ProjectPartnerBudgetOptions(
    partnerId = partnerId,
    officeAndAdministrationFlatRate = officeAdministrationFlatRate,
    travelAndAccommodationFlatRate = travelAccommodationFlatRate,
    staffCostsFlatRate = staffCostsFlatRate,
)

fun Iterable<ProjectPartnerBudgetOptionsEntity>.toModel() = map { it.toModel() }
