package io.cloudflight.jems.server.payments.entity

import io.cloudflight.jems.server.programme.entity.fund.ProgrammeFundEntity
import io.cloudflight.jems.server.project.entity.ProjectEntity
import io.cloudflight.jems.server.project.entity.lumpsum.ProjectLumpSumEntity
import java.math.BigDecimal
import javax.persistence.*
import javax.validation.constraints.NotNull

@Entity(name = "payment")
class PaymentToProjectEntity (

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @OneToOne(optional = false)
    @JoinColumn(name = "project_id")
    @field:NotNull
    val project: ProjectEntity,

    @Column(name = "order_nr")
    @field:NotNull
    val orderNr: Int,

    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumns(
        JoinColumn(name = "order_nr", insertable = false, updatable = false),
        JoinColumn(name = "project_id", insertable = false, updatable = false),
    )
    @field:NotNull
    val lumpSum: ProjectLumpSumEntity,

    @OneToOne
    @JoinColumn(name = "programme_fund_id")
    @field:NotNull
    val fund: ProgrammeFundEntity,

    @Column(name = "programme_fund_id", insertable = false, updatable = false)
    @field:NotNull
    val programmeFundId: Long,

    @Column(name = "programme_lump_sum_id")
    @field:NotNull
    val programmeLumpSumId: Long,

    @Column
    @field:NotNull
    val partnerId: Long,

    @Column
    val amountApprovedPerFund: BigDecimal?
)
