package io.cloudflight.jems.server.payments.service.account.reconciliation

import io.cloudflight.jems.server.payments.model.account.finance.reconciliation.PaymentAccountReconciliation
import io.cloudflight.jems.server.payments.model.account.finance.reconciliation.ReconciledAmountUpdate

interface PaymentAccountReconciliationPersistence {

    fun updateReconciliation(paymentAccountId: Long,  reconciliationUpdate: ReconciledAmountUpdate): PaymentAccountReconciliation

    fun getByPaymentAccountId(paymentAccountId: Long): List<PaymentAccountReconciliation>

}
