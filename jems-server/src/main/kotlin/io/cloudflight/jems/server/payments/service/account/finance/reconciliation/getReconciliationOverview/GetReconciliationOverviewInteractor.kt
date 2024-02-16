package io.cloudflight.jems.server.payments.service.account.finance.reconciliation.getReconciliationOverview

import io.cloudflight.jems.server.payments.model.account.finance.reconciliation.ReconciledAmountPerPriority

interface GetReconciliationOverviewInteractor {

    fun getReconciliationOverview(paymentAccountId: Long): List<ReconciledAmountPerPriority>

}
