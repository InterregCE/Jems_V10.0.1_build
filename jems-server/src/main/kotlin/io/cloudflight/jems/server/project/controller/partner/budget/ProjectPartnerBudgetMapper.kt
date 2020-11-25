package io.cloudflight.jems.server.project.controller.partner.budget

import io.cloudflight.jems.api.project.dto.partner.budget.ProjectPartnerBudgetOptionsDto
import io.cloudflight.jems.server.project.service.budget.model.ProjectPartnerBudgetOptions

fun ProjectPartnerBudgetOptions.toProjectPartnerBudgetOptionsDto() = ProjectPartnerBudgetOptionsDto(
    officeAndAdministrationFlatRate = this.officeAndAdministrationFlatRate,
    travelAndAccommodationFlatRate = travelAndAccommodationFlatRate,
    staffCostsFlatRate = this.staffCostsFlatRate,
)

fun ProjectPartnerBudgetOptionsDto.toModel(partnerId: Long) = ProjectPartnerBudgetOptions(
    partnerId = partnerId,
    officeAndAdministrationFlatRate = this.officeAndAdministrationFlatRate,
    travelAndAccommodationFlatRate = travelAndAccommodationFlatRate,
    staffCostsFlatRate = this.staffCostsFlatRate,
)
