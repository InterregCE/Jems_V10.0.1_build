package io.cloudflight.jems.server.payments.service.paymentApplicationsToEc.updatePaymentApplicationToEcDetail

import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEcDetail
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEcSummaryUpdate

interface UpdatePaymentApplicationToEcDetailInteractor {

    fun updatePaymentApplicationToEc(paymentApplicationToEcUpdate: PaymentApplicationToEcSummaryUpdate): PaymentApplicationToEcDetail

}
