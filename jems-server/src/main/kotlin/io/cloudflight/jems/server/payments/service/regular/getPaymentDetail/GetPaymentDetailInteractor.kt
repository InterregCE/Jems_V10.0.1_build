package io.cloudflight.jems.server.payments.service.regular.getPaymentDetail

import io.cloudflight.jems.server.payments.model.regular.PaymentDetail

interface GetPaymentDetailInteractor {

    fun getPaymentDetail(paymentId: Long): PaymentDetail
}
