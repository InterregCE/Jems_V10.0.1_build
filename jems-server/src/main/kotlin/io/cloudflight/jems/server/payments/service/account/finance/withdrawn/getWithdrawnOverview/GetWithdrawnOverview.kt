package io.cloudflight.jems.server.payments.service.account.finance.withdrawn.getWithdrawnOverview

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.payments.authorization.CanRetrievePaymentsAccount
import io.cloudflight.jems.server.payments.model.account.finance.withdrawn.AmountWithdrawnPerPriority
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetWithdrawnOverview(
    private val paymentsAccountWithdrawnOverviewService: PaymentsAccountWithdrawnOverviewService
) : GetWithdrawnOverviewInteractor {

    @CanRetrievePaymentsAccount
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetWithdrawnOverviewException::class)
    override fun getWithdrawnOverview(paymentAccountId: Long): List<AmountWithdrawnPerPriority> =
        paymentsAccountWithdrawnOverviewService.getWithdrawnOverview(paymentAccountId)

}
