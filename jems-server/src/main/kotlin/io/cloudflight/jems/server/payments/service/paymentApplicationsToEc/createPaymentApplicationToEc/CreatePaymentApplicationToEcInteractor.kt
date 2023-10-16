package io.cloudflight.jems.server.payments.service.paymentApplicationsToEc.createPaymentApplicationToEc

import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEcCreate
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEcDetail

interface CreatePaymentApplicationToEcInteractor {

    fun createPaymentApplicationToEc(paymentApplicationToEc: PaymentApplicationToEcCreate): PaymentApplicationToEcDetail

}
