package io.cloudflight.jems.server.payments.model.ec

import java.math.BigDecimal

data class PaymentToEcCorrectionLinkingUpdate (
    val correctedPublicContribution: BigDecimal,
    val correctedAutoPublicContribution: BigDecimal,
    val correctedPrivateContribution: BigDecimal,
    val correctedTotalEligibleWithoutArt94or95: BigDecimal,
    val correctedUnionContribution: BigDecimal,
    val correctedFundAmount: BigDecimal,
    val comment: String
)
