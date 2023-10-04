package io.cloudflight.jems.server.payments.service.paymentApplicationsToEc.linkedPaymentsToEc.selectPayment

interface SelectPaymentToEcInteractor {

    fun selectPaymentToEcPayment(paymentId: Long, ecPaymentId: Long)

}
