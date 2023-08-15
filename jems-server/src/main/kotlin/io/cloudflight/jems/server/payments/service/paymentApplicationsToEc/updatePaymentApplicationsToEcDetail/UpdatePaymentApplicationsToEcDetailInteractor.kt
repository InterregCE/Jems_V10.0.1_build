package io.cloudflight.jems.server.payments.service.paymentApplicationsToEc.updatePaymentApplicationsToEcDetail

import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationsToEcDetail
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationsToEcUpdate

interface UpdatePaymentApplicationsToEcDetailInteractor {

    fun updatePaymentApplicationsToEc(paymentApplicationstoEcUpdate: PaymentApplicationsToEcUpdate): PaymentApplicationsToEcDetail

}
