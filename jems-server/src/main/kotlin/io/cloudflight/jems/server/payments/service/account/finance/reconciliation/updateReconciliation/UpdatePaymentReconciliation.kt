package io.cloudflight.jems.server.payments.service.account.finance.reconciliation.updateReconciliation

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.payments.authorization.CanUpdatePaymentsAccount
import io.cloudflight.jems.server.payments.model.account.PaymentAccount
import io.cloudflight.jems.server.payments.model.account.finance.reconciliation.ReconciledAmountUpdate
import io.cloudflight.jems.server.payments.service.account.PaymentAccountPersistence
import io.cloudflight.jems.server.payments.service.account.reconciliation.PaymentAccountReconciliationPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdatePaymentReconciliation(
    private val reconciliationPersistence: PaymentAccountReconciliationPersistence,
    private val paymentAccountPersistence: PaymentAccountPersistence,
    private val generalValidator: GeneralValidatorService
) : UpdatePaymentReconciliationInteractor {

    @CanUpdatePaymentsAccount
    @Transactional
    @ExceptionWrapper(UpdatePaymentReconciliationException::class)
    override fun updatePaymentReconciliation(
        paymentAccountId: Long,
        reconciliationUpdate: ReconciledAmountUpdate
    ) {
        val paymentAccount = paymentAccountPersistence.getByPaymentAccountId(paymentAccountId)

        validateAccountIsDraft(paymentAccount)
        validateReconciliationComment(reconciliationUpdate)

        reconciliationPersistence.updateReconciliation(paymentAccountId, reconciliationUpdate)
    }

    private fun validateAccountIsDraft(paymentAccount: PaymentAccount) {
        if(paymentAccount.status.isFinished()) {
            throw PaymentAccountNotInDraftException()
        }
    }
    private fun validateReconciliationComment(reconciliationUpdate: ReconciledAmountUpdate) {
        generalValidator.throwIfAnyIsInvalid(
            generalValidator.maxLength(reconciliationUpdate.comment, 500, "comment")
        )
    }
}
