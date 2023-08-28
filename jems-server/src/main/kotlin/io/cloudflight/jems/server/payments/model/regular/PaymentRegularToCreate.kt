package io.cloudflight.jems.server.payments.model.regular

import java.math.BigDecimal

data class PaymentRegularToCreate(
    val projectId: Long,
    val partnerPayments: List<PaymentPartnerToCreate>,
    val fundId: Long,
    val amountApprovedPerFund: BigDecimal,
)
