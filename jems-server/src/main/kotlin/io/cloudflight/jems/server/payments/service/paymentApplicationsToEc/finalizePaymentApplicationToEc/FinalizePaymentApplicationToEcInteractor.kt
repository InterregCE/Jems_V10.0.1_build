package io.cloudflight.jems.server.payments.service.paymentApplicationsToEc.finalizePaymentApplicationToEc

import io.cloudflight.jems.server.payments.model.regular.PaymentEcStatus

interface FinalizePaymentApplicationToEcInteractor {

    fun finalizePaymentApplicationToEc(paymentId: Long): PaymentEcStatus

}
