package io.cloudflight.jems.server.payments.service.account.listPaymentAccount

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.payments.authorization.CanRetrievePaymentsAccount
import io.cloudflight.jems.server.payments.model.account.PaymentAccountOverview
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ListPaymentAccount(
    private val paymentAccountsListService: PaymentAccountsListService
) : ListPaymentAccountInteractor {

    @CanRetrievePaymentsAccount
    @Transactional(readOnly = true)
    @ExceptionWrapper(ListPaymentAccountException::class)
    override fun listPaymentAccount(): List<PaymentAccountOverview> =
        paymentAccountsListService.listPaymentAccount()
}
