package io.cloudflight.jems.server.payments.model.ec

import java.math.BigDecimal

data class PaymentToEcLinkingUpdate (
    val correctedPublicContribution: BigDecimal,
    val correctedAutoPublicContribution: BigDecimal,
    val correctedPrivateContribution: BigDecimal
)
