package io.cloudflight.jems.server.payments.service.paymentApplicationsToEc.getPaymentApplicationToEcDetail

import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEcDetail

interface GetPaymentApplicationToEcDetailInteractor {
    fun getPaymentApplicationToEcDetail(id: Long): PaymentApplicationToEcDetail
}
