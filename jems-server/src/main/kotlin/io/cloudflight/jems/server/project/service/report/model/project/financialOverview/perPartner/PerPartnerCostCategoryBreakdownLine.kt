package io.cloudflight.jems.server.project.service.report.model.project.financialOverview.perPartner

import io.cloudflight.jems.server.project.service.budget.model.BudgetCostsCalculationResultFull
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole

data class PerPartnerCostCategoryBreakdownLine(
    val partnerId: Long,
    val partnerNumber: Int,
    val partnerAbbreviation: String,
    val partnerRole: ProjectPartnerRole,
    val country: String?,

    val officeAndAdministrationOnStaffCostsFlatRate: Int?,
    val officeAndAdministrationOnDirectCostsFlatRate: Int?,
    val travelAndAccommodationOnStaffCostsFlatRate: Int?,
    val staffCostsFlatRate: Int?,
    val otherCostsOnStaffCostsFlatRate: Int?,

    val current: BudgetCostsCalculationResultFull,
    val afterControl: BudgetCostsCalculationResultFull,
)
