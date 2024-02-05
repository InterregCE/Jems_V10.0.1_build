package io.cloudflight.jems.server.payments.service.account.corrections.deselectCorrection

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.payments.authorization.CanUpdatePaymentsAccount
import io.cloudflight.jems.server.payments.service.account.corrections.PaymentAccountCorrectionLinkingPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DeselectCorrectionFromPaymentAccount(
    private val correctionLinkingPersistence: PaymentAccountCorrectionLinkingPersistence
) : DeselectCorrectionFromPaymentAccountInteractor {

    @CanUpdatePaymentsAccount
    @Transactional
    @ExceptionWrapper(DeselectCorrectionFromPaymentAccountException::class)
    override fun deselectCorrection(correctionId: Long) {
        val paymentAccountStatus = correctionLinkingPersistence.getCorrectionExtension(correctionId).paymentAccountStatus

        if (paymentAccountStatus?.isFinished() == true)
            throw PaymentAccountNotInDraftException()

        correctionLinkingPersistence.deselectCorrectionFromPaymentAccountAndResetFields(setOf(correctionId))
    }
}
