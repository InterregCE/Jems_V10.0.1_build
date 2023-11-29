package io.cloudflight.jems.api.payments.dto

import java.math.BigDecimal

data class EcPaymentCorrectionExtensionDTO(
    val correctionId: Long,
    val ecPaymentId: Long?,
    val ecPaymentStatus: PaymentEcStatusDTO?,
    val comment: String?,

    val fundAmount: BigDecimal,

    val publicContribution: BigDecimal,
    val correctedPublicContribution: BigDecimal,

    val autoPublicContribution: BigDecimal,
    val correctedAutoPublicContribution: BigDecimal,

    val privateContribution: BigDecimal,
    val correctedPrivateContribution: BigDecimal,
)
