package io.cloudflight.jems.server.payments.service.account.updatePaymentAccount

import io.cloudflight.jems.server.payments.model.account.PaymentAccount
import io.cloudflight.jems.server.payments.model.account.PaymentAccountUpdate

interface UpdatePaymentAccountInteractor {

    fun updatePaymentAccount(paymentAccountId: Long, paymentAccount: PaymentAccountUpdate): PaymentAccount

}
