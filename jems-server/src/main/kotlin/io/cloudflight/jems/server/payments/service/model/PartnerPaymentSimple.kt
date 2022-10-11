package io.cloudflight.jems.server.payments.service.model

import java.math.BigDecimal

data class PartnerPaymentSimple(
    val fundId: Long,
    val amountApprovedPerPartner: BigDecimal,
)
