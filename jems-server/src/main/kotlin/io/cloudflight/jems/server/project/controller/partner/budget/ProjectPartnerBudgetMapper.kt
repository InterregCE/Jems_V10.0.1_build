package io.cloudflight.jems.server.project.controller.partner.budget

import io.cloudflight.jems.api.project.dto.partner.budget.ProjectPartnerBudgetOptionsDto
import io.cloudflight.jems.server.project.service.budget.model.ProjectPartnerBudgetOptions

fun ProjectPartnerBudgetOptions.toProjectPartnerBudgetOptionsDto() = ProjectPartnerBudgetOptionsDto(
    officeAndAdministrationOnStaffCostsFlatRate = this.officeAndAdministrationOnStaffCostsFlatRate,
    travelAndAccommodationOnStaffCostsFlatRate = travelAndAccommodationOnStaffCostsFlatRate,
    staffCostsFlatRate = this.staffCostsFlatRate,
    otherCostsOnStaffCostsFlatRate = otherCostsOnStaffCostsFlatRate
)

fun ProjectPartnerBudgetOptionsDto.toProjectPartnerBudgetOptions(partnerId: Long) = ProjectPartnerBudgetOptions(
    partnerId = partnerId,
    officeAndAdministrationOnStaffCostsFlatRate = this.officeAndAdministrationOnStaffCostsFlatRate,
    travelAndAccommodationOnStaffCostsFlatRate = travelAndAccommodationOnStaffCostsFlatRate,
    staffCostsFlatRate = this.staffCostsFlatRate,
    otherCostsOnStaffCostsFlatRate = this.otherCostsOnStaffCostsFlatRate,
)
