package io.cloudflight.jems.server.payments.service.paymentApplicationsToEc.createPaymentApplicationToEc

import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEcDetail
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEcSummaryUpdate

interface CreatePaymentApplicationToEcInteractor {

    fun createPaymentApplicationToEc(paymentApplicationToEcUpdate: PaymentApplicationToEcSummaryUpdate): PaymentApplicationToEcDetail

}
