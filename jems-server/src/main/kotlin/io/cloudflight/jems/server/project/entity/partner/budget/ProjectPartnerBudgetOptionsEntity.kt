package io.cloudflight.jems.server.project.entity.partner.budget

import javax.persistence.Entity
import javax.persistence.Id

@Entity(name = "project_partner_budget_options")
data class ProjectPartnerBudgetOptionsEntity(

    @Id
    val partnerId: Long,
    var officeAndAdministrationOnStaffCostsFlatRate: Int? = null,
    var officeAndAdministrationOnDirectCostsFlatRate: Int? = null,
    var travelAndAccommodationOnStaffCostsFlatRate: Int? = null,
    var staffCostsFlatRate: Int? = null,
    var otherCostsOnStaffCostsFlatRate: Int? = null

)
