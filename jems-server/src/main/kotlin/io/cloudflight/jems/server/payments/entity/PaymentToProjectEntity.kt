package io.cloudflight.jems.server.payments.entity

import io.cloudflight.jems.server.programme.entity.fund.ProgrammeFundEntity
import io.cloudflight.jems.server.project.entity.ProjectEntity
import java.math.BigDecimal
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.ManyToOne
import javax.persistence.JoinColumn
import javax.persistence.Entity
import javax.persistence.Column
import javax.validation.constraints.NotNull

@Entity(name = "payment")
class PaymentToProjectEntity (

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(optional = false)
    @JoinColumn(name = "project_id")
    @field:NotNull
    val project: ProjectEntity,

    @Column(name = "order_nr")
    @field:NotNull
    val orderNr: Int,

    @ManyToOne
    @JoinColumn(name = "programme_fund_id")
    @field:NotNull
    val fund: ProgrammeFundEntity,

    @Column(name = "programme_lump_sum_id")
    @field:NotNull
    val programmeLumpSumId: Long,

    @Column
    @field:NotNull
    val partnerId: Long,

    @Column
    val amountApprovedPerFund: BigDecimal?
)
