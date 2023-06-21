package io.cloudflight.jems.server.payments.entity

import java.math.BigDecimal
import javax.persistence.Embedded
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.validation.constraints.NotNull

@Entity(name = "payment_contribution_meta")
class PaymentContributionMetaEntity (

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @field:NotNull
    val projectId: Long,

    @field:NotNull
    val partnerId: Long,

    @Embedded
    val lumpSum: PaymentLumpSumEntity,

    @field:NotNull
    val partnerContribution: BigDecimal,
    @field:NotNull
    val publicContribution: BigDecimal,
    @field:NotNull
    val automaticPublicContribution: BigDecimal,
    @field:NotNull
    val privateContribution: BigDecimal,
)
