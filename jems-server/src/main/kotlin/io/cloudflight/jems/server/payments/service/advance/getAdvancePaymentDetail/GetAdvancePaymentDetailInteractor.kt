package io.cloudflight.jems.server.payments.service.advance.getAdvancePaymentDetail

import io.cloudflight.jems.server.payments.model.advance.AdvancePaymentDetail

interface GetAdvancePaymentDetailInteractor {

    fun getPaymentDetail(paymentId: Long): AdvancePaymentDetail

}
