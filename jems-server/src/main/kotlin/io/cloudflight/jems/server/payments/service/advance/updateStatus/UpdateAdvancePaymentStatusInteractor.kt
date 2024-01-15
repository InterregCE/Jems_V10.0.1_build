package io.cloudflight.jems.server.payments.service.advance.updateStatus

import io.cloudflight.jems.server.payments.model.advance.AdvancePaymentStatus

interface UpdateAdvancePaymentStatusInteractor {

    fun updateStatus(paymentId: Long, status: AdvancePaymentStatus)
}
