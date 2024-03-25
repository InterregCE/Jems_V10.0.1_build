package io.cloudflight.jems.server.payments.entity

import io.cloudflight.jems.server.payments.model.regular.PaymentSearchRequestScoBasis
import java.math.BigDecimal
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.MapsId
import javax.validation.constraints.NotNull

@Entity(name = "payment_to_ec_extension")
class PaymentToEcExtensionEntity (

    @Id
    val paymentId: Long,

    @ManyToOne
    @JoinColumn(name = "payment_id")
    @MapsId
    @field:NotNull
    val payment: PaymentEntity,

    @ManyToOne(optional = true)
    var paymentApplicationToEc: PaymentApplicationToEcEntity? = null,

    @field:NotNull val totalEligibleWithoutSco: BigDecimal,
    @field:NotNull var correctedTotalEligibleWithoutSco: BigDecimal,

    @field:NotNull val fundAmountUnionContribution: BigDecimal,
    @field:NotNull var correctedFundAmountUnionContribution: BigDecimal,

    @field:NotNull val fundAmountPublicContribution: BigDecimal,
    @field:NotNull var correctedFundAmountPublicContribution: BigDecimal,

    @field:NotNull
    val partnerContribution: BigDecimal,

    @field:NotNull val publicContribution: BigDecimal,
    @field:NotNull var correctedPublicContribution: BigDecimal,

    @field:NotNull val autoPublicContribution: BigDecimal,
    @field:NotNull var correctedAutoPublicContribution: BigDecimal,

    @field:NotNull val privateContribution: BigDecimal,
    @field:NotNull var correctedPrivateContribution: BigDecimal,

    var comment: String?,

    @Enumerated(EnumType.STRING)
    var finalScoBasis: PaymentSearchRequestScoBasis?,

    // temporary field to save values before big re-calculation MP2-4546
    val beforeFixPartnerContribution: BigDecimal? = null,

)
