package io.cloudflight.jems.server.project.service.budget.model

data class ProjectPartnerBudgetOptions(

    val partnerId: Long,

    val officeAndAdministrationFlatRate: Int? = null,
    val travelAndAccommodationFlatRate: Int? = null,

    val staffCostsFlatRate: Int? = null

) {
    fun isEmpty() =
        officeAndAdministrationFlatRate == null
            && staffCostsFlatRate == null
            && travelAndAccommodationFlatRate == null
}
