package io.cloudflight.jems.server.payments.service.ecPayment.linkToPayment.selectPayment

interface SelectPaymentToEcInteractor {

    fun selectPaymentToEcPayment(paymentId: Long, ecPaymentId: Long)

}
