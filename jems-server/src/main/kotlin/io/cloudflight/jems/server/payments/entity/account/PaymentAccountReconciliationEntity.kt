package io.cloudflight.jems.server.payments.entity.account

import io.cloudflight.jems.server.programme.entity.ProgrammePriorityEntity
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.ManyToOne
import javax.validation.constraints.NotNull

@Entity(name = "payment_account_reconciliation")
class PaymentAccountReconciliationEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @ManyToOne
    @field:NotNull
    val paymentAccount: PaymentAccountEntity,

    @ManyToOne
    @field:NotNull
    val priorityAxis: ProgrammePriorityEntity,

    @field:NotNull
    var totalComment: String,

    @field:NotNull
    var aaComment: String,

    @field:NotNull
    var ecComment: String,

)
