package io.cloudflight.jems.server.project.entity.partner.budget

import java.math.BigDecimal
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity(name = "project_partner_budget_staff_cost")
data class ProjectPartnerBudgetStaffCost(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    override val id: Long?,

    @Column(nullable = false)
    override val partnerId: Long,

    @Column(nullable = false)
    override val numberOfUnits: BigDecimal,

    @Column(nullable = false)
    override val pricePerUnit: BigDecimal

): Budget
