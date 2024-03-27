package io.cloudflight.jems.server.payments.service.account.finance.correction.getOverview

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.payments.authorization.CanRetrievePaymentsAccount
import io.cloudflight.jems.server.payments.model.account.finance.PaymentAccountAmountSummary
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetPaymentAccountCurrentOverview(
    private val paymentAccountCorrectionsOverviewService: PaymentAccountCorrectionsOverviewService,
) : GetPaymentAccountCurrentOverviewInteractor {

    @CanRetrievePaymentsAccount
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetPaymentAccountCurrentOverviewException::class)
    override fun getCurrentOverview(paymentAccountId: Long): PaymentAccountAmountSummary =
        paymentAccountCorrectionsOverviewService.getCurrentOverview(paymentAccountId)

}
