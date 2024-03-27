package io.cloudflight.jems.server.payments.service.account.finance.correction.deselectCorrection

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.payments.authorization.CanUpdatePaymentsAccount
import io.cloudflight.jems.server.payments.model.account.finance.correction.PaymentAccountCorrectionExtension
import io.cloudflight.jems.server.payments.service.account.finance.correction.PaymentAccountCorrectionLinkingPersistence
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
        val correction = correctionLinkingPersistence.getCorrectionExtension(correctionId)

        if (correction.isLinkedToFinishedAccountOrNotLinked())
            throw PaymentAccountNotInDraftException()

        correctionLinkingPersistence.deselectCorrectionFromPaymentAccountAndResetFields(correctionId)
    }

    private fun PaymentAccountCorrectionExtension.isLinkedToFinishedAccountOrNotLinked() =
        paymentAccountStatus == null || paymentAccountStatus.isFinished()

}
