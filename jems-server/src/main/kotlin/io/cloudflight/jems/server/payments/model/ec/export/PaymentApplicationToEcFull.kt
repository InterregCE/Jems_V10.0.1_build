package io.cloudflight.jems.server.payments.model.ec.export

import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEcSummary
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcAmountSummary
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcCorrectionLinking
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcPayment

data class PaymentApplicationToEcFull(
    val id: Long,
    val paymentApplicationToEcSummary: PaymentApplicationToEcSummary,
    val paymentToEcAmountSummary: PaymentToEcAmountSummary,
    val corrections: List<PaymentToEcCorrectionLinking>,
    val regularProjectPayments: List<PaymentToEcPayment>
)
