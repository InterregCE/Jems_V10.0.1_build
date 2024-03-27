package io.cloudflight.jems.server.payments.model.regular.toCreate

import java.math.BigDecimal

data class PaymentPartnerToCreate(
    val partnerId: Long,
    val partnerReportId: Long?,
    val amountApprovedPerPartner: BigDecimal,

    val partnerAbbreviationIfFtls: String?,
    val partnerNameInOriginalLanguageIfFtls: String?,
    val partnerNameInEnglishIfFtls: String?,
)
