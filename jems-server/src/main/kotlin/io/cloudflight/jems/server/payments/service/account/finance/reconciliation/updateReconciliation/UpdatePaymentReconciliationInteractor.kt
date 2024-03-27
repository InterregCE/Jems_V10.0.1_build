package io.cloudflight.jems.server.payments.service.account.finance.reconciliation.updateReconciliation

import io.cloudflight.jems.server.payments.model.account.finance.reconciliation.ReconciledAmountUpdate

interface UpdatePaymentReconciliationInteractor {

    fun updatePaymentReconciliation(paymentAccountId: Long, reconciliationUpdate: ReconciledAmountUpdate)

}
