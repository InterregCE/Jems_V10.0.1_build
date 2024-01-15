package io.cloudflight.jems.server.payments.entity

import io.cloudflight.jems.server.payments.model.regular.PaymentSearchRequestScoBasis
import io.cloudflight.jems.server.project.entity.auditAndControl.AuditControlCorrectionEntity
import java.math.BigDecimal
import javax.persistence.*
import javax.validation.constraints.NotNull

@Entity(name = "payment_to_ec_correction_extension")
class PaymentToEcCorrectionExtensionEntity (

    @Id
    val correctionId: Long,

    @ManyToOne
    @JoinColumn(name = "correction_id")
    @MapsId
    @field:NotNull
    val correction: AuditControlCorrectionEntity,

    @ManyToOne(optional = true)
    var paymentApplicationToEc: PaymentApplicationToEcEntity? = null,

    @field:NotNull
    val fundAmount: BigDecimal,
    @field:NotNull
    var correctedFundAmount: BigDecimal,

    @field:NotNull
    val publicContribution: BigDecimal,
    @field:NotNull
    var correctedPublicContribution: BigDecimal,

    @field:NotNull
    val autoPublicContribution: BigDecimal,
    @field:NotNull
    var correctedAutoPublicContribution: BigDecimal,

    @field:NotNull
    val privateContribution: BigDecimal,
    @field:NotNull
    var correctedPrivateContribution: BigDecimal,

    var comment: String?,

    @Enumerated(EnumType.STRING)
    var finalScoBasis: PaymentSearchRequestScoBasis?,

    @Column(name = "total_eligible_without_art_94_or_95")
    @field:NotNull
    var totalEligibleWithoutArt94or95: BigDecimal,

    @Column(name = "corrected_total_eligible_without_art_94_or_95")
    @field:NotNull
    var correctedTotalEligibleWithoutArt94or95: BigDecimal,

    @field:NotNull
    val unionContribution: BigDecimal,
    @field:NotNull
    var correctedUnionContribution: BigDecimal,
)
