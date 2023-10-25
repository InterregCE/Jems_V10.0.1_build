package io.cloudflight.jems.server.payments.service.ecPayment.linkToPayment.updatePayment

import io.cloudflight.jems.server.payments.model.ec.PaymentToEcLinkingUpdate

interface UpdateLinkedPaymentInteractor {

    fun updateLinkedPayment(paymentId: Long, updatePaymentForEc: PaymentToEcLinkingUpdate)
}
