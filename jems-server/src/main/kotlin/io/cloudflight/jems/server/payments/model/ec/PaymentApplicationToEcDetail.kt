package io.cloudflight.jems.server.payments.model.ec

import io.cloudflight.jems.server.payments.model.regular.PaymentEcStatus

data class PaymentApplicationToEcDetail (
    val id: Long,
    val status: PaymentEcStatus,
    val paymentApplicationToEcSummary: PaymentApplicationToEcSummary
)

