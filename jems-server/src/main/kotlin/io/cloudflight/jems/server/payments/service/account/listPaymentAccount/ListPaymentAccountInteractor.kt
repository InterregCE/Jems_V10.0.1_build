package io.cloudflight.jems.server.payments.service.account.listPaymentAccount

import io.cloudflight.jems.server.payments.model.account.PaymentAccountOverview

interface ListPaymentAccountInteractor {

    fun listPaymentAccount(): List<PaymentAccountOverview>

}
