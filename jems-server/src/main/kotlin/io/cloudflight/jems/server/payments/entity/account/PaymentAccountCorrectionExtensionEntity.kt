package io.cloudflight.jems.server.payments.entity.account

import io.cloudflight.jems.server.project.entity.auditAndControl.AuditControlCorrectionEntity
import java.math.BigDecimal
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.MapsId
import javax.validation.constraints.NotNull

@Entity(name = "payment_account_correction_extension")
class PaymentAccountCorrectionExtensionEntity(

    @Id
    val correctionId: Long,

    @ManyToOne
    @JoinColumn(name = "correction_id")
    @MapsId
    @field:NotNull
    val correction: AuditControlCorrectionEntity,

    @ManyToOne(optional = true)
    var paymentAccount: PaymentAccountEntity? = null,

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

    @field:NotNull
    var comment: String,
)
