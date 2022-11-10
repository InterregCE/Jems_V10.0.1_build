package io.cloudflight.jems.server.payments.service.getAdvancePaymentDetail

import io.cloudflight.jems.server.payments.service.model.AdvancePaymentDetail

interface GetAdvancePaymentDetailInteractor {

    fun getPaymentDetail(paymentId: Long): AdvancePaymentDetail

}
