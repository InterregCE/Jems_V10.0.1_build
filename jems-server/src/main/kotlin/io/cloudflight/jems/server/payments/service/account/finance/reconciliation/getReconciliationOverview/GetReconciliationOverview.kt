package io.cloudflight.jems.server.payments.service.account.finance.reconciliation.getReconciliationOverview

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.payments.authorization.CanRetrievePaymentsAccount
import io.cloudflight.jems.server.payments.model.account.finance.reconciliation.ReconciledAmountPerPriority
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetReconciliationOverview(
    private val paymentAccountReconciliationOverviewService: PaymentAccountReconciliationOverviewService
) : GetReconciliationOverviewInteractor {

    @CanRetrievePaymentsAccount
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetReconciliationOverviewException::class)
    override fun getReconciliationOverview(paymentAccountId: Long): List<ReconciledAmountPerPriority> =
        paymentAccountReconciliationOverviewService.getReconciliationOverview(paymentAccountId)

}
