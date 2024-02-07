package io.cloudflight.jems.api.payments.dto.account

import java.math.BigDecimal

data class PaymentAccountCorrectionLinkingUpdateDTO(
    val correctedPublicContribution: BigDecimal,
    val correctedAutoPublicContribution: BigDecimal,
    val correctedPrivateContribution: BigDecimal,
    val correctedFundAmount: BigDecimal,
    val comment: String,
)
