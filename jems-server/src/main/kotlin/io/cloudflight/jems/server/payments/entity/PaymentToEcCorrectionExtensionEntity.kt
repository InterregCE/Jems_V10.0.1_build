package io.cloudflight.jems.server.payments.entity

import io.cloudflight.jems.server.payments.model.regular.PaymentSearchRequestScoBasis
import io.cloudflight.jems.server.project.entity.auditAndControl.AuditControlCorrectionEntity
import java.math.BigDecimal
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.MapsId
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

)
