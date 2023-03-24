package io.cloudflight.jems.api.project.dto.report.project.financialOverview

import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerRoleDTO

data class PerPartnerCostCategoryBreakdownLineDTO(
    val partnerId: Long,
    val partnerNumber: Int,
    val partnerAbbreviation: String,
    val partnerRole: ProjectPartnerRoleDTO,
    val country: String?,

    val officeAndAdministrationOnStaffCostsFlatRate: Int?,
    val officeAndAdministrationOnDirectCostsFlatRate: Int?,
    val travelAndAccommodationOnStaffCostsFlatRate: Int?,
    val staffCostsFlatRate: Int?,
    val otherCostsOnStaffCostsFlatRate: Int?,

    val current: BudgetCostsCalculationResultFullDTO,
    val deduction: BudgetCostsCalculationResultFullDTO,
)
