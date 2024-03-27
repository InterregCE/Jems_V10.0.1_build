package io.cloudflight.jems.server.payments.service.account.finance.getAmountSummary

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.payments.authorization.CanRetrievePaymentsAccount
import io.cloudflight.jems.server.payments.model.account.finance.PaymentAccountAmountSummary
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetPaymentAccountAmountSummary(
    private val paymentAccountAmountSummaryService: PaymentAccountAmountSummaryService
) : GetPaymentAccountAmountSummaryInteractor {

    @CanRetrievePaymentsAccount
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetPaymentAccountAmountSummaryException::class)
    override fun getSummaryOverview(paymentAccountId: Long): PaymentAccountAmountSummary =
        paymentAccountAmountSummaryService.getSummaryOverview(paymentAccountId)

}
