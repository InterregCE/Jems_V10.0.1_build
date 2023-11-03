package io.cloudflight.jems.server.payments.entity

import io.cloudflight.jems.server.programme.entity.ProgrammePriorityEntity
import java.math.BigDecimal
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.ManyToOne
import javax.validation.constraints.NotNull

@Entity(name = "payment_application_to_ec_priority_axis_cumulative_overview")
class PaymentToEcPriorityAxisCumulativeOverviewEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @ManyToOne
    @field:NotNull
    val paymentApplicationToEc: PaymentApplicationToEcEntity,

    @ManyToOne(optional = true)
    val priorityAxis: ProgrammePriorityEntity?,

    @field:NotNull
    val totalEligibleExpenditure: BigDecimal,

    @field:NotNull
    val totalUnionContribution: BigDecimal,

    @field:NotNull
    val totalPublicContribution: BigDecimal,
)
