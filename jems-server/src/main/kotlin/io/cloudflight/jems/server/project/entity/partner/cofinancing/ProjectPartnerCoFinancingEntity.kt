package io.cloudflight.jems.server.project.entity.partner.cofinancing

import io.cloudflight.jems.server.programme.entity.ProgrammeFundEntity
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.validation.constraints.NotNull

@Entity(name = "project_partner_co_financing")
data class ProjectPartnerCoFinancingEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @field:NotNull
    val partnerId: Long,

    @field:NotNull
    val percentage: Int,

    @ManyToOne
    @JoinColumn(name = "programme_fund_id")
    val programmeFund: ProgrammeFundEntity?

)
