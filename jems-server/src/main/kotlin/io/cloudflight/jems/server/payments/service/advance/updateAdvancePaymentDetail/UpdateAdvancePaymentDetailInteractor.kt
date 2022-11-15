package io.cloudflight.jems.server.payments.service.advance.updateAdvancePaymentDetail

import io.cloudflight.jems.server.payments.model.advance.AdvancePaymentDetail
import io.cloudflight.jems.server.payments.model.advance.AdvancePaymentUpdate

interface UpdateAdvancePaymentDetailInteractor {

    fun updateDetail(paymentDetail: AdvancePaymentUpdate): AdvancePaymentDetail
}
