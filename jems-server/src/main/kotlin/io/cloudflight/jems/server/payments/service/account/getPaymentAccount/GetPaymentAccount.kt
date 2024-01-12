package io.cloudflight.jems.server.payments.service.account.getPaymentAccount

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.payments.authorization.CanRetrievePaymentsAccount
import io.cloudflight.jems.server.payments.model.account.PaymentAccount
import io.cloudflight.jems.server.payments.service.account.PaymentAccountPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetPaymentAccount(
    private val paymentAccountPersistence: PaymentAccountPersistence
) : GetPaymentAccountInteractor {

    @CanRetrievePaymentsAccount
    @Transactional
    @ExceptionWrapper(GetPaymentAccountException::class)
    override fun getPaymentAccount(paymentAccountId: Long): PaymentAccount =
        paymentAccountPersistence.getByPaymentAccountId(paymentAccountId)

}
