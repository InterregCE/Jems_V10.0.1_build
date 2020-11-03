package io.cloudflight.jems.server.project.controller

import io.cloudflight.jems.api.project.dto.partner.budget.ProjectPartnerBudgetOptionsDto
import io.cloudflight.jems.server.project.entity.partner.budget.ProjectPartnerBudgetOptions

fun ProjectPartnerBudgetOptions.toProjectPartnerBudgetOptionsDto() = ProjectPartnerBudgetOptionsDto(
        officeAdministrationFlatRate = this.officeAdministrationFlatRate,
        staffCostsFlatRate = this.staffCostsFlatRate
)
