package io.cloudflight.jems.server.payments.service.account.finance

import io.cloudflight.jems.server.payments.model.account.PaymentAccountOverviewContribution
import io.cloudflight.jems.server.payments.model.account.finance.reconciliation.ReconciledPriority
import io.cloudflight.jems.server.payments.model.account.finance.reconciliation.ReconciledScenario
import io.cloudflight.jems.server.payments.model.account.finance.withdrawn.CorrectionAmountWithdrawn
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcAmountSummaryLine

interface PaymentAccountFinancePersistence {

    fun getCorrectionsOnlyDeductionsAndNonClericalMistake(
        fundId: Long,
        accountingYearId: Long,
    ): Iterable<CorrectionAmountWithdrawn>

    fun getReconciliationOverview(paymentAccountId: Long, scenario: ReconciledScenario): List<ReconciledPriority>

    /** Summary */
    fun getTotalsForFinishedEcPayments(ecPaymentIds: Set<Long>): Map<Long?, PaymentToEcAmountSummaryLine>

    fun getOverviewTotalsForFinishedPaymentAccounts(): Map<Long, PaymentAccountOverviewContribution>

}
