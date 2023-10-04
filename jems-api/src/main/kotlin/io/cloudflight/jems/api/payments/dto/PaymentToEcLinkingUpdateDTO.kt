package io.cloudflight.jems.api.payments.dto

import java.math.BigDecimal

data class PaymentToEcLinkingUpdateDTO (
    val correctedPublicContribution: BigDecimal,
    val correctedAutoPublicContribution: BigDecimal,
    val correctedPrivateContribution: BigDecimal
)
