package io.cloudflight.jems.server.payments.service.ecPayment.updatePaymentApplicationToEcDetail

import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEcDetail
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEcSummaryUpdate

interface UpdatePaymentApplicationToEcDetailInteractor {

    fun updatePaymentApplicationToEc(
        paymentApplicationId: Long,
        paymentApplicationToEcUpdate: PaymentApplicationToEcSummaryUpdate
    ): PaymentApplicationToEcDetail

}
