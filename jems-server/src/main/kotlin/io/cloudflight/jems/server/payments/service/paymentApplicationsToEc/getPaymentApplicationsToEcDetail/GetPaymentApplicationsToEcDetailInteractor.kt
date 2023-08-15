package io.cloudflight.jems.server.payments.service.paymentApplicationsToEc.getPaymentApplicationsToEcDetail

import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationsToEcDetail

interface GetPaymentApplicationsToEcDetailInteractor {
    fun getPaymentApplicationsToEcDetail(id: Long): PaymentApplicationsToEcDetail
}
