package io.cloudflight.jems.server.payments.entity

import io.cloudflight.jems.server.payments.model.regular.PaymentSearchRequestScoBasis
import io.cloudflight.jems.server.programme.entity.ProgrammePriorityEntity
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

@Entity(name = "payment_application_to_ec_priority_axis_overview")
class PaymentToEcPriorityAxisOverviewEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne
    @JoinColumn(name = "payment_application_to_ec_id")
    @field:NotNull
    val paymentApplicationToEc: PaymentApplicationToEcEntity,

    @Enumerated(EnumType.STRING)
    @field:NotNull
    val type: PaymentSearchRequestScoBasis,

    @ManyToOne(optional = true)
    val priorityAxis: ProgrammePriorityEntity?,

    @field:NotNull
    val totalEligibleExpenditure: BigDecimal,

    @field:NotNull
    val totalUnionContribution: BigDecimal,

    @field:NotNull
    val totalPublicContribution: BigDecimal,
)
