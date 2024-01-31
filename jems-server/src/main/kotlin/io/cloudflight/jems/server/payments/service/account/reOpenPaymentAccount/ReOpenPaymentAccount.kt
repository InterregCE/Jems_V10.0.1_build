package io.cloudflight.jems.server.payments.service.account.reOpenPaymentAccount

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.payments.authorization.CanUpdatePaymentsAccount
import io.cloudflight.jems.server.payments.model.account.PaymentAccount
import io.cloudflight.jems.server.payments.model.account.PaymentAccountStatus
import io.cloudflight.jems.server.payments.service.account.PaymentAccountPersistence
import io.cloudflight.jems.server.payments.service.paymentAccountsReOpened
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ReOpenPaymentAccount(
    private val paymentAccountPersistence: PaymentAccountPersistence,
    private val auditPublisher: ApplicationEventPublisher
) : ReOpenPaymentAccountInteractor {

    @CanUpdatePaymentsAccount
    @Transactional
    @ExceptionWrapper(ReOpenPaymentAccountException::class)
    override fun reOpenPaymentAccount(paymentAccountId: Long): PaymentAccountStatus {
        val paymentAccount = paymentAccountPersistence.getByPaymentAccountId(paymentAccountId)

        validateAccount(paymentAccount)

        return paymentAccountPersistence.reOpenPaymentAccount(paymentAccountId).also {
            auditPublisher.publishEvent(paymentAccountsReOpened(context = this, paymentAccount))
        }
    }

    private fun validateAccount(paymentAccount: PaymentAccount) {
        if (paymentAccount.status != PaymentAccountStatus.FINISHED) {
            throw PaymentAccountNotSubmittedException()
        }
    }

}
