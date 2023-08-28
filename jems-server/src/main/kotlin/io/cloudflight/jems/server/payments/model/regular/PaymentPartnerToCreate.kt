package io.cloudflight.jems.server.payments.model.regular

import java.math.BigDecimal

data class PaymentPartnerToCreate(
    val partnerId: Long,
    val partnerReportId: Long?,
    val amountApprovedPerPartner: BigDecimal,
)
