package io.cloudflight.jems.server.payments.model.regular

import java.math.BigDecimal

data class PartnerPaymentSimple(
    val fundId: Long,
    val amountApprovedPerPartner: BigDecimal,
)
