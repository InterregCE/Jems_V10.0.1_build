package io.cloudflight.jems.server.payments.service.ecPayment.finalizePaymentApplicationToEc

import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEcDetail

interface FinalizePaymentApplicationToEcInteractor {

    fun finalizePaymentApplicationToEc(paymentId: Long): PaymentApplicationToEcDetail

}
