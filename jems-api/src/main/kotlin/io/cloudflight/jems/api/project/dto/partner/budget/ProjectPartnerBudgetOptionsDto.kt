package io.cloudflight.jems.api.project.dto.partner.budget

import javax.validation.constraints.Max
import javax.validation.constraints.Min

data class ProjectPartnerBudgetOptionsDto(
    @field:Max(15)
    @field:Min(1)
    val officeAdministrationFlatRate: Int?,
    @field:Max(20)
    @field:Min(1)
    val staffCostsFlatRate: Int?
)
