package io.cloudflight.jems.server.payments.entity

import io.cloudflight.jems.server.payments.service.model.PaymentType
import io.cloudflight.jems.server.programme.entity.fund.ProgrammeFundEntity
import io.cloudflight.jems.server.project.entity.ProjectEntity
import java.math.BigDecimal
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.validation.constraints.NotNull

@Entity(name = "payment")
class PaymentEntity (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Enumerated(EnumType.STRING)
    @field:NotNull
    val type: PaymentType,

    @ManyToOne(optional = false)
    @JoinColumn(name = "project_id")
    @field:NotNull
    val project: ProjectEntity,

    @field:NotNull
    val orderNr: Int,

    @ManyToOne
    @JoinColumn(name = "programme_fund_id")
    @field:NotNull
    val fund: ProgrammeFundEntity,

    @field:NotNull
    val programmeLumpSumId: Long,

    val amountApprovedPerFund: BigDecimal?,
)
