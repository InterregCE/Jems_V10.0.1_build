package io.cloudflight.jems.server.payments.service.account.finance.withdrawn.getWithdrawnOverview

import io.cloudflight.jems.server.payments.model.account.finance.withdrawn.AmountWithdrawnPerPriority

interface GetWithdrawnOverviewInteractor {

    fun getWithdrawnOverview(paymentAccountId: Long): List<AmountWithdrawnPerPriority>

}
