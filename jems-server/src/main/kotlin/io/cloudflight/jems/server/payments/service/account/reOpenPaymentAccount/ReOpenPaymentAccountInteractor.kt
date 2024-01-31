package io.cloudflight.jems.server.payments.service.account.reOpenPaymentAccount

import io.cloudflight.jems.server.payments.model.account.PaymentAccountStatus

interface ReOpenPaymentAccountInteractor {

    fun reOpenPaymentAccount(paymentAccountId: Long): PaymentAccountStatus

}
