package io.cloudflight.jems.server.payments.service.paymentApplicationsToEc.updatePaymentApplicationToEcDetail

import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEcDetail
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEcUpdate

interface UpdatePaymentApplicationToEcDetailInteractor {

    fun updatePaymentApplicationToEc(paymentApplicationToEcUpdate: PaymentApplicationToEcUpdate): PaymentApplicationToEcDetail

}
