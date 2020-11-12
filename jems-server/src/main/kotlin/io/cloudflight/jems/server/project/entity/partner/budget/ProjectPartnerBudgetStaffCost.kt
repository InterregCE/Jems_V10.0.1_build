package io.cloudflight.jems.server.project.entity.partner.budget

import javax.persistence.Embedded
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.validation.constraints.NotNull

@Entity(name = "project_partner_budget_staff_cost")
data class ProjectPartnerBudgetStaffCost(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    override val id: Long = 0,

    @field:NotNull
    override val partnerId: Long,

    @Embedded
    override val budget: Budget

): CommonBudget
