package io.cloudflight.jems.server.payments.service.account.finance

import io.cloudflight.jems.server.payments.model.account.finance.withdrawn.CorrectionAmountWithdrawn
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcAmountSummaryLine

interface PaymentAccountFinancePersistence {

    fun getCorrectionsOnlyDeductionsAndNonClericalMistake(
        fundId: Long,
        accountingYearId: Long,
    ): Iterable<CorrectionAmountWithdrawn>

    /** Summary */
    fun getTotalsForFinishedEcPayments(ecPaymentIds: Set<Long>): Map<Long?, PaymentToEcAmountSummaryLine>

}
