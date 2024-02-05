package io.cloudflight.jems.api.payments.dto.account

import java.math.BigDecimal

data class PaymentAccountCorrectionExtensionDTO(
    val correctionId: Long,
    val paymentAccountId: Long?,
    val paymentAccountStatus: PaymentAccountStatusDTO?,
    val comment: String?,

    val fundAmount: BigDecimal,
    val correctedFundAmount: BigDecimal,

    val publicContribution: BigDecimal,
    val correctedPublicContribution: BigDecimal,

    val autoPublicContribution: BigDecimal,
    val correctedAutoPublicContribution: BigDecimal,

    val privateContribution: BigDecimal,
    val correctedPrivateContribution: BigDecimal,
)
