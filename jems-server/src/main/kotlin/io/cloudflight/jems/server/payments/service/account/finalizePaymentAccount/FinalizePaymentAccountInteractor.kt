package io.cloudflight.jems.server.payments.service.account.finalizePaymentAccount

import io.cloudflight.jems.server.payments.model.account.PaymentAccountStatus

interface FinalizePaymentAccountInteractor {

    fun finalizePaymentAccount(paymentAccountId: Long): PaymentAccountStatus

}
