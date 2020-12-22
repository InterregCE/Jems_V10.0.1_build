package io.cloudflight.jems.server.project.repository.partner.budget

import io.cloudflight.jems.server.project.entity.partner.budget.ProjectPartnerBudgetOptionsEntity
import io.cloudflight.jems.server.project.service.budget.model.ProjectPartnerBudgetOptions

fun ProjectPartnerBudgetOptions.toProjectPartnerBudgetOptionsEntity() = ProjectPartnerBudgetOptionsEntity(
    partnerId = partnerId,
    officeAndAdministrationOnStaffCostsFlatRate = officeAndAdministrationOnStaffCostsFlatRate,
    travelAndAccommodationOnStaffCostsFlatRate = travelAndAccommodationOnStaffCostsFlatRate,
    staffCostsFlatRate = staffCostsFlatRate,
    otherCostsOnStaffCostsFlatRate = otherCostsOnStaffCostsFlatRate
)

fun ProjectPartnerBudgetOptionsEntity.toProjectPartnerBudgetOptions() = ProjectPartnerBudgetOptions(
    partnerId = partnerId,
    officeAndAdministrationOnStaffCostsFlatRate = officeAndAdministrationOnStaffCostsFlatRate,
    travelAndAccommodationOnStaffCostsFlatRate = travelAndAccommodationOnStaffCostsFlatRate,
    staffCostsFlatRate = staffCostsFlatRate,
    otherCostsOnStaffCostsFlatRate = otherCostsOnStaffCostsFlatRate
)

fun Iterable<ProjectPartnerBudgetOptionsEntity>.toProjectPartnerBudgetOptions() = map { it.toProjectPartnerBudgetOptions() }
