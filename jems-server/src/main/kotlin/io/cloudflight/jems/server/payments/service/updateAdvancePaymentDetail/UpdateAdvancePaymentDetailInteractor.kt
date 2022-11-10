package io.cloudflight.jems.server.payments.service.updateAdvancePaymentDetail

import io.cloudflight.jems.server.payments.service.model.AdvancePaymentDetail
import io.cloudflight.jems.server.payments.service.model.AdvancePaymentUpdate

interface UpdateAdvancePaymentDetailInteractor {

    fun updateDetail(paymentDetail: AdvancePaymentUpdate): AdvancePaymentDetail
}
