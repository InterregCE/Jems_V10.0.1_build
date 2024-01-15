package io.cloudflight.jems.api.payments.dto

import java.math.BigDecimal

data class PaymentToEcCorrectionLinkingUpdateDTO (
    val correctedPublicContribution: BigDecimal,
    val correctedAutoPublicContribution: BigDecimal,
    val correctedPrivateContribution: BigDecimal,
    val correctedTotalEligibleWithoutArt94or95: BigDecimal,
    val correctedUnionContribution: BigDecimal,
    val correctedFundAmount: BigDecimal,
    val comment: String?
)
