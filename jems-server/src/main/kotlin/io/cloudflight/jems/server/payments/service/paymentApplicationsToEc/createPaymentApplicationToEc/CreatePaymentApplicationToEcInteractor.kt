package io.cloudflight.jems.server.payments.service.paymentApplicationsToEc.createPaymentApplicationToEc

import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEcDetail
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEcUpdate

interface CreatePaymentApplicationToEcInteractor {

    fun createPaymentApplicationToEc(paymentApplicationToEcUpdate: PaymentApplicationToEcUpdate): PaymentApplicationToEcDetail

}
