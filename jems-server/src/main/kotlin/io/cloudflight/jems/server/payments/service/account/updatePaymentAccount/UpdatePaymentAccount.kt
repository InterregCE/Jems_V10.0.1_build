package io.cloudflight.jems.server.payments.service.account.updatePaymentAccount

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.payments.authorization.CanUpdatePaymentsAccount
import io.cloudflight.jems.server.payments.model.account.PaymentAccount
import io.cloudflight.jems.server.payments.model.account.PaymentAccountUpdate
import io.cloudflight.jems.server.payments.service.account.PaymentAccountPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdatePaymentAccount(
    private val paymentAccountPersistence: PaymentAccountPersistence,
    private val validator: GeneralValidatorService
) : UpdatePaymentAccountInteractor {

    @CanUpdatePaymentsAccount
    @Transactional
    @ExceptionWrapper(UpdatePaymentAccountException::class)
    override fun updatePaymentAccount(paymentAccountId: Long, paymentAccount: PaymentAccountUpdate): PaymentAccount {
        validateLengthOfFields(paymentAccount)
        return paymentAccountPersistence.updatePaymentAccount(paymentAccountId, paymentAccount)
    }

    private fun validateLengthOfFields(paymentAccount: PaymentAccountUpdate) {
        validator.throwIfAnyIsInvalid(
            validator.maxLength(paymentAccount.nationalReference, 50, "nationalReference"),
            validator.maxLength(paymentAccount.sfcNumber, 50, "sfcNumber"),
            validator.maxLength(paymentAccount.comment, 5000, "comment")
        )
    }

}
