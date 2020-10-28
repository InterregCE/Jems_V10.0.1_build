package io.cloudflight.jems.server.project.entity.partner.cofinancing

import io.cloudflight.jems.server.programme.entity.ProgrammeFund
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne

@Entity(name = "project_partner_co_financing")
data class ProjectPartnerCoFinancing(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "partner_id", nullable = false)
    val partnerId: Long,

    @Column(nullable = false)
    val percentage: Int,

    @ManyToOne
    @JoinColumn(name = "programme_fund_id")
    val programmeFund: ProgrammeFund?

)
