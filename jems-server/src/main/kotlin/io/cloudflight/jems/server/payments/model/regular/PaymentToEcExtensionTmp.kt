package io.cloudflight.jems.server.payments.model.regular

import java.math.BigDecimal

data class PaymentToEcExtensionTmp(
    val paymentToEcId: Long?,
    val partnerContribution: BigDecimal,
    val publicContribution: BigDecimal,
    val correctedPublicContribution: BigDecimal,
    val autoPublicContribution: BigDecimal,
    val correctedAutoPublicContribution: BigDecimal,
    val privateContribution: BigDecimal,
    val correctedPrivateContribution: BigDecimal,
)
