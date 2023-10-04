package io.cloudflight.jems.server.payments.service.paymentApplicationsToEc.linkedPaymentsToEc.updatePayment

import io.cloudflight.jems.server.payments.model.ec.PaymentToEcLinkingUpdate

interface UpdateLinkedPaymentInteractor {

    fun updateLinkedPayment(paymentId: Long, updatePaymentForEc: PaymentToEcLinkingUpdate)
}
