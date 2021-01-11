package io.cloudflight.jems.server.project.service.partner.model

data class ProjectPartnerBudgetOptions(
    val partnerId: Long,
    val officeAndAdministrationOnStaffCostsFlatRate: Int? = null,
    val travelAndAccommodationOnStaffCostsFlatRate: Int? = null,
    val staffCostsFlatRate: Int? = null,
    val otherCostsOnStaffCostsFlatRate: Int? = null
){
    fun isEmpty() =
        officeAndAdministrationOnStaffCostsFlatRate == null
            && staffCostsFlatRate == null
            && travelAndAccommodationOnStaffCostsFlatRate == null
            && otherCostsOnStaffCostsFlatRate == null
}
