package io.cloudflight.jems.server.payments.service.account.getPaymentAccount

import io.cloudflight.jems.server.payments.model.account.PaymentAccount

interface GetPaymentAccountInteractor {

    fun getPaymentAccount(paymentAccountId: Long): PaymentAccount

}
