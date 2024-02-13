package io.cloudflight.jems.api.payments.dto.account.finance.correction

import io.cloudflight.jems.api.payments.dto.account.PaymentAccountStatusDTO
import java.math.BigDecimal

data class PaymentAccountCorrectionExtensionDTO(
    val correctionId: Long,
    val paymentAccountId: Long?,
    val paymentAccountStatus: PaymentAccountStatusDTO?,
    val comment: String?,

    val fundAmount: BigDecimal,

    val publicContribution: BigDecimal,
    val correctedPublicContribution: BigDecimal,

    val autoPublicContribution: BigDecimal,
    val correctedAutoPublicContribution: BigDecimal,

    val privateContribution: BigDecimal,
    val correctedPrivateContribution: BigDecimal,
)
