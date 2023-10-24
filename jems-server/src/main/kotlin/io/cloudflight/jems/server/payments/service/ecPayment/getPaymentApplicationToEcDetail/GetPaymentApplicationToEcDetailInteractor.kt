package io.cloudflight.jems.server.payments.service.ecPayment.getPaymentApplicationToEcDetail

import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEcDetail

interface GetPaymentApplicationToEcDetailInteractor {
    fun getPaymentApplicationToEcDetail(id: Long): PaymentApplicationToEcDetail
}
