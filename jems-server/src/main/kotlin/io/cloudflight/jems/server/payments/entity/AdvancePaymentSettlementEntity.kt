package io.cloudflight.jems.server.payments.entity

import java.math.BigDecimal
import java.time.LocalDate
import javax.persistence.Id
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Entity
import javax.persistence.ManyToOne
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.validation.constraints.NotNull

@Entity(name = "payment_advance_settlement")
class AdvancePaymentSettlementEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    val number: Int,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "advance_payment_id")
    @field:NotNull
    val advancePayment: AdvancePaymentEntity,

    @field:NotNull
    val amountSettled: BigDecimal,

    @field:NotNull
    val settlementDate: LocalDate,

    val comment: String?

)