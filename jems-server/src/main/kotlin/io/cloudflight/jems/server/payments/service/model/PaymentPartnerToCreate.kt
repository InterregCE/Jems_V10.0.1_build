package io.cloudflight.jems.server.payments.service.model

import java.math.BigDecimal

data class PaymentPartnerToCreate(
        val partnerId: Long,
        val amountApprovedPerPartner: BigDecimal
)
