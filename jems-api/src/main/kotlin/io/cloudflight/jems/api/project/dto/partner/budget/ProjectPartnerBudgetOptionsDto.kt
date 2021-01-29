package io.cloudflight.jems.api.project.dto.partner.budget

data class ProjectPartnerBudgetOptionsDto(
    val officeAndAdministrationOnStaffCostsFlatRate: Int? = null,
    val officeAndAdministrationOnDirectCostsFlatRate: Int? = null,
    val travelAndAccommodationOnStaffCostsFlatRate: Int? = null,
    val staffCostsFlatRate: Int? = null,
    val otherCostsOnStaffCostsFlatRate: Int? = null
)
