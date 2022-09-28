package io.cloudflight.jems.server.payments.entity

import java.math.BigDecimal
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.validation.constraints.NotNull

@Entity(name = "payment_partner")
class PaymentPartnerEntity (

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(optional = false)
    @JoinColumn(name = "payment_id")
    @field:NotNull
    val payment: PaymentEntity,

    @field:NotNull
    val partnerId: Long,

    val amountApprovedPerPartner: BigDecimal?
)
