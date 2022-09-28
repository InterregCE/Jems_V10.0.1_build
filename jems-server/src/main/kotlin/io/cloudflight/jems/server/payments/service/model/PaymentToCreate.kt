package io.cloudflight.jems.server.payments.service.model

import java.math.BigDecimal

data class PaymentToCreate(
    val programmeLumpSumId: Long,
    val partnerPayments: List<PaymentPartnerToCreate>,
    val amountApprovedPerFund: BigDecimal
)
