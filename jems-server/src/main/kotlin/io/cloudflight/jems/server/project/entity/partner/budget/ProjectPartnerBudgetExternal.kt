package io.cloudflight.jems.server.project.entity.partner.budget

import javax.persistence.Column
import javax.persistence.Embedded
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity(name = "project_partner_budget_external")
data class ProjectPartnerBudgetExternal(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    override val id: Long? = null,

    @Column(nullable = false)
    override val partnerId: Long,

    @Embedded
    override val budget: Budget?

): CommonBudget
