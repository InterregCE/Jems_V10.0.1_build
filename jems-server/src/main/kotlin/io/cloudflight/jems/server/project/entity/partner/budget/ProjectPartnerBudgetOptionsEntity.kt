package io.cloudflight.jems.server.project.entity.partner.budget

import javax.persistence.Entity
import javax.persistence.Id
import javax.validation.constraints.NotNull

@Entity(name = "project_partner_budget_options")
data class ProjectPartnerBudgetOptionsEntity(

    @Id
    @field:NotNull
    val partnerId: Long,
    var officeAndAdministrationOnStaffCostsFlatRate: Int? = null,
    var travelAndAccommodationOnStaffCostsFlatRate: Int? = null,
    var staffCostsFlatRate: Int? = null,
    var otherCostsOnStaffCostsFlatRate: Int? = null

)
