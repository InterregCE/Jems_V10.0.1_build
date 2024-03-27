package io.cloudflight.jems.server.payments.model.account.finance.correction

import java.math.BigDecimal

data class PaymentAccountCorrectionLinkingUpdate(
    val correctedPublicContribution: BigDecimal,
    val correctedAutoPublicContribution: BigDecimal,
    val correctedPrivateContribution: BigDecimal,
    val comment: String,
)
