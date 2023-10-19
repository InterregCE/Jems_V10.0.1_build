package io.cloudflight.jems.server.payments.service.paymentApplicationsToEc.finalizePaymentApplicationToEc

import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEcDetail

interface FinalizePaymentApplicationToEcInteractor {

    fun finalizePaymentApplicationToEc(paymentId: Long): PaymentApplicationToEcDetail

}
