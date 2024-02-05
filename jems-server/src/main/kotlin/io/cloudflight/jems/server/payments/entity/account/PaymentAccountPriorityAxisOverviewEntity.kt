package io.cloudflight.jems.server.payments.entity.account

import io.cloudflight.jems.server.payments.model.account.PaymentAccountOverviewType
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

@Entity(name = "payment_account_priority_axis_overview")
class PaymentAccountPriorityAxisOverviewEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @ManyToOne
    @JoinColumn(name = "payment_account_id")
    @field:NotNull
    val paymentAccount: PaymentAccountEntity,

    @Enumerated(EnumType.STRING)
    @field:NotNull
    val type: PaymentAccountOverviewType,

    @ManyToOne(optional = true)
    val priorityAxis: ProgrammePriorityEntity?,

    @field:NotNull
    val totalEligibleExpenditure: BigDecimal,

    @field:NotNull
    val totalPublicContribution: BigDecimal,
)
