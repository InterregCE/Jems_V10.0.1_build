package io.cloudflight.jems.server.project.entity.partner.budget

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id

@Entity(name = "project_partner_budget_options")
data class ProjectPartnerBudgetOptions(

    @Id
    @Column(nullable = false)
    val partnerId: Long,

    @Column
    var officeAdministrationFlatRate: Int?,

    @Column
    var staffCostsFlatRate: Int?
)
