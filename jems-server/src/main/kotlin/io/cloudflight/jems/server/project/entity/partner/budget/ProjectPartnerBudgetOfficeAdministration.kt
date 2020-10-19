package io.cloudflight.jems.server.project.entity.partner.budget

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id

@Entity(name = "project_partner_budget_office_administration")
data class ProjectPartnerBudgetOfficeAdministration(

    @Id
    @Column(nullable = false)
    val partnerId: Long,

    @Column(nullable = false)
    val flatRate: Int

)
