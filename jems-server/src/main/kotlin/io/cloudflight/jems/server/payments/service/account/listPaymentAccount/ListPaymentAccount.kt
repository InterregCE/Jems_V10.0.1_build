package io.cloudflight.jems.server.payments.service.account.listPaymentAccount

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.payments.authorization.CanRetrievePaymentsAccount
import io.cloudflight.jems.server.payments.model.account.PaymentAccountOverview
import io.cloudflight.jems.server.payments.repository.account.toOverviewModel
import io.cloudflight.jems.server.payments.service.account.PaymentAccountPersistence
import io.cloudflight.jems.server.payments.service.account.finance.PaymentAccountFinancePersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ListPaymentAccount(
    private val paymentAccountPersistence: PaymentAccountPersistence,
    private val paymentAccountFinancePersistence: PaymentAccountFinancePersistence,
) : ListPaymentAccountInteractor {

    @CanRetrievePaymentsAccount
    @Transactional(readOnly = true)
    @ExceptionWrapper(ListPaymentAccountException::class)
    override fun listPaymentAccount(): List<PaymentAccountOverview> {
        val paymentAccountsByFund = paymentAccountPersistence.getAllAccounts().groupBy { it.fund }
        val overviewContribution = paymentAccountFinancePersistence.getOverviewContributionForPaymentAccounts()

        return paymentAccountsByFund.toOverviewModel(overviewContribution)
    }

}
