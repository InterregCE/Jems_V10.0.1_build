package io.cloudflight.jems.server.project.service.budget.model

data class ProjectPartnerBudgetOptions(

    val partnerId: Long,

    val officeAdministrationFlatRate: Int? = null,
    val staffCostsFlatRate: Int? = null

)
