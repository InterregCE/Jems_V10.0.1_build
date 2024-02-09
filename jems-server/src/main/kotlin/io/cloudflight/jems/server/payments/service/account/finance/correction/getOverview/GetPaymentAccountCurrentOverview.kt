package io.cloudflight.jems.server.payments.service.account.finance.correction.getOverview

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.payments.authorization.CanRetrievePaymentsAccount
import io.cloudflight.jems.server.payments.model.account.PaymentAccountAmountSummary
import io.cloudflight.jems.server.payments.service.account.PaymentAccountPersistence
import io.cloudflight.jems.server.payments.service.account.finance.correction.PaymentAccountCorrectionLinkingPersistence
import io.cloudflight.jems.server.payments.service.account.finance.correction.sumUp
import io.cloudflight.jems.server.payments.service.account.finance.correction.sumUpProperColumns
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetPaymentAccountCurrentOverview(
    private val paymentAccountPersistence: PaymentAccountPersistence,
    private val correctionLinkingPersistence: PaymentAccountCorrectionLinkingPersistence,
) : GetPaymentAccountCurrentOverviewInteractor {

    @CanRetrievePaymentsAccount
    @Transactional
    @ExceptionWrapper(GetPaymentAccountCurrentOverviewException::class)
    override fun getCurrentOverview(paymentAccountId: Long): PaymentAccountAmountSummary {
        val paymentAccount = paymentAccountPersistence.getByPaymentAccountId(paymentAccountId)

        val currentOverview = if (paymentAccount.status.isFinished())
            correctionLinkingPersistence.getTotalsForFinishedPaymentAccount(paymentAccountId)
        else
            correctionLinkingPersistence.calculateOverviewForDraftPaymentAccount(paymentAccountId).sumUpProperColumns()

        return PaymentAccountAmountSummary(
            amountsGroupedByPriority = currentOverview.values.toList(),
            totals = currentOverview.values.sumUp()
        )
    }
}
