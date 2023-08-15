package io.cloudflight.jems.server.payments.model.ec

import io.cloudflight.jems.server.payments.model.regular.PaymentEcStatus

data class PaymentApplicationsToEcDetail (
    val id: Long,
    val status: PaymentEcStatus,
    val paymentApplicationsToEcSummary: PaymentApplicationsToEcSummary
)

