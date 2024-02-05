package io.cloudflight.jems.server.payments.service.account.corrections.updateCorrection

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.payments.authorization.CanUpdatePaymentsAccount
import io.cloudflight.jems.server.payments.model.account.PaymentAccountCorrectionExtension
import io.cloudflight.jems.server.payments.model.account.PaymentAccountCorrectionLinkingUpdate
import io.cloudflight.jems.server.payments.service.account.corrections.PaymentAccountCorrectionLinkingPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdateLinkedCorrectionToPaymentAccount(
    private val correctionLinkingPersistence: PaymentAccountCorrectionLinkingPersistence
) : UpdateLinkedCorrectionToPaymentAccountInteractor {

    @CanUpdatePaymentsAccount
    @Transactional
    @ExceptionWrapper(UpdateLinkedCorrectionToPaymentAccountException::class)
    override fun updateCorrection(correctionId: Long, correctionLinkingUpdate: PaymentAccountCorrectionLinkingUpdate): PaymentAccountCorrectionExtension {
        val paymentAccountStatus = correctionLinkingPersistence.getCorrectionExtension(correctionId).paymentAccountStatus
        if (paymentAccountStatus?.isFinished() == true)
            throw PaymentAccountNotInDraftException()

        return correctionLinkingPersistence.updateCorrectionLinkedToPaymentAccountCorrectedAmounts(
            correctionId = correctionId, correctionLinkingUpdate = correctionLinkingUpdate,
        )
    }
}
