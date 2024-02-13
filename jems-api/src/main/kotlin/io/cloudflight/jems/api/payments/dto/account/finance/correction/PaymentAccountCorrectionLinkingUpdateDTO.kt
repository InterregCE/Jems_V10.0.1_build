package io.cloudflight.jems.api.payments.dto.account.finance.correction

import java.math.BigDecimal

data class PaymentAccountCorrectionLinkingUpdateDTO(
    val correctedPublicContribution: BigDecimal,
    val correctedAutoPublicContribution: BigDecimal,
    val correctedPrivateContribution: BigDecimal,
    val comment: String,
)
