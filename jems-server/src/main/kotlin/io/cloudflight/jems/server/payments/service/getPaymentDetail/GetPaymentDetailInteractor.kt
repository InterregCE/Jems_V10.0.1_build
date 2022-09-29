package io.cloudflight.jems.server.payments.service.getPaymentDetail

import io.cloudflight.jems.server.payments.service.model.PaymentDetail

interface GetPaymentDetailInteractor {

    fun getPaymentDetail(paymentId: Long): PaymentDetail
}
