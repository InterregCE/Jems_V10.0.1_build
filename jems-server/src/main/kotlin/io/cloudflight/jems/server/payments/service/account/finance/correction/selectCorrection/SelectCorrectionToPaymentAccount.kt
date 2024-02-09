package io.cloudflight.jems.server.payments.service.account.finance.correction.selectCorrection

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.payments.authorization.CanUpdatePaymentsAccount
import io.cloudflight.jems.server.payments.service.account.PaymentAccountPersistence
import io.cloudflight.jems.server.payments.service.account.finance.correction.PaymentAccountCorrectionLinkingPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SelectCorrectionToPaymentAccount(
    private val paymentAccountPersistence: PaymentAccountPersistence,
    private val correctionLinkingPersistence: PaymentAccountCorrectionLinkingPersistence
) : SelectCorrectionToPaymentAccountInteractor {

    @CanUpdatePaymentsAccount
    @Transactional
    @ExceptionWrapper(SelectCorrectionToPaymentAccountException::class)
    override fun selectCorrection(correctionId: Long, paymentAccountId: Long) {
        val paymentAccount = paymentAccountPersistence.getByPaymentAccountId(paymentAccountId)
        if (paymentAccount.status.isFinished())
            throw PaymentAccountNotInDraftException()

        val availableCorrections = correctionLinkingPersistence.getCorrectionIdsAvailableForPaymentAccounts(
            paymentAccount.fund.id
        )

        if (!availableCorrections.contains(correctionId))
            throw CorrectionNotAvailableForSelectionException()

        correctionLinkingPersistence.selectCorrectionToPaymentAccount(
            correctionIds = setOf(correctionId),
            paymentAccountId = paymentAccountId
        )
    }
}
