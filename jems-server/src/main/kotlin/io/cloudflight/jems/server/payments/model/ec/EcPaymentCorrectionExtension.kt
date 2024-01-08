package io.cloudflight.jems.server.payments.model.ec

import io.cloudflight.jems.server.payments.model.regular.PaymentEcStatus
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControlStatus
import java.math.BigDecimal

data class EcPaymentCorrectionExtension(
    val correctionId: Long,
    val ecPaymentId: Long?,
    val ecPaymentStatus: PaymentEcStatus?,
    val auditControlStatus :AuditControlStatus,
    val comment: String?,

    val fundAmount: BigDecimal,
    val correctedFundAmount: BigDecimal,

    val publicContribution: BigDecimal,
    val correctedPublicContribution: BigDecimal,

    val autoPublicContribution: BigDecimal,
    val correctedAutoPublicContribution: BigDecimal,

    val privateContribution: BigDecimal,
    val correctedPrivateContribution: BigDecimal,

    val totalEligibleWithoutArt94or95: BigDecimal,
    val correctedTotalEligibleWithoutArt94or95: BigDecimal,

    val unionContribution: BigDecimal,
    val correctedUnionContribution: BigDecimal,
)
