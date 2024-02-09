package io.cloudflight.jems.server.payments.service.account.finance

import io.cloudflight.jems.server.payments.model.account.finance.withdrawn.CorrectionAmountWithdrawn

interface PaymentAccountFinancePersistence {

    fun getCorrectionsOnlyDeductionsAndNonClericalMistake(
        fundId: Long,
        accountingYearId: Long,
    ): Iterable<CorrectionAmountWithdrawn>

}
