package io.cloudflight.jems.server.payments.model.account

import java.math.BigDecimal

data class PaymentAccountCorrectionLinkingUpdate (
    val correctedFundAmount: BigDecimal,
    val correctedPublicContribution: BigDecimal,
    val correctedAutoPublicContribution: BigDecimal,
    val correctedPrivateContribution: BigDecimal,
    val comment: String
)
