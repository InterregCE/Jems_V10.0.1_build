package io.cloudflight.jems.server.payments.entity

import io.cloudflight.jems.server.payments.model.regular.PaymentType
import io.cloudflight.jems.server.programme.entity.fund.ProgrammeFundEntity
import io.cloudflight.jems.server.project.entity.ProjectEntity
import io.cloudflight.jems.server.project.entity.lumpsum.ProjectLumpSumEntity
import java.math.BigDecimal
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.JoinColumns
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
    val projectCustomIdentifier: String,
    @field:NotNull
    val projectAcronym: String,

    @ManyToOne(optional = false)
    // join 2 columns
    @JoinColumns(
        JoinColumn(name = "project_lump_sum_id", referencedColumnName = "project_id"),
        JoinColumn(name = "order_nr", referencedColumnName = "order_nr")
    )
    @field:NotNull
    val projectLumpSum: ProjectLumpSumEntity,

    @ManyToOne
    @JoinColumn(name = "programme_fund_id")
    @field:NotNull
    val fund: ProgrammeFundEntity,

    val amountApprovedPerFund: BigDecimal?,

)
