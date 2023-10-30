package io.cloudflight.jems.server.payments.entity

import java.math.BigDecimal
import javax.persistence.EmbeddedId
import javax.persistence.Entity
import javax.validation.constraints.NotNull

@Entity(name = "payment_application_to_ec_priority_axis_cumulative_overview")
class PaymentToEcPriorityAxisCumulativeOverviewEntity(

    @EmbeddedId
    val id: PaymentEcCumulativeId,

    @field:NotNull
    val totalEligibleExpenditure: BigDecimal,

    @field:NotNull
    val totalUnionContribution: BigDecimal,

    @field:NotNull
    val totalPublicContribution: BigDecimal,
)