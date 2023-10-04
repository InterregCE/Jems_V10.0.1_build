package io.cloudflight.jems.server.payments.model.ec

import io.cloudflight.jems.server.payments.model.regular.PaymentEcStatus

data class PaymentToEcExtension(
    val paymentId: Long,
    val ecPaymentId: Long?,
    val ecPaymentStatus: PaymentEcStatus?,
)
