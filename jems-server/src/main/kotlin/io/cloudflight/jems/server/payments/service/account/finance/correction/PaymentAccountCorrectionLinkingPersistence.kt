package io.cloudflight.jems.server.payments.service.account.finance.correction

import io.cloudflight.jems.server.payments.model.account.finance.PaymentAccountAmountSummaryLine
import io.cloudflight.jems.server.payments.model.account.finance.PaymentAccountAmountSummaryLineTmp
import io.cloudflight.jems.server.payments.model.account.finance.correction.PaymentAccountCorrectionExtension
import io.cloudflight.jems.server.payments.model.account.finance.correction.PaymentAccountCorrectionLinkingUpdate
import io.cloudflight.jems.server.project.service.auditAndControl.model.ProjectCorrectionFinancialDescription

interface PaymentAccountCorrectionLinkingPersistence {

    fun getCorrectionExtension(correctionId: Long): PaymentAccountCorrectionExtension

    fun selectCorrectionToPaymentAccount(correctionIds: Set<Long>, paymentAccountId: Long)

    fun deselectCorrectionFromPaymentAccountAndResetFields(correctionId: Long)

    fun updateCorrectionLinkedToPaymentAccountCorrectedAmounts(
        correctionId: Long,
        correctionLinkingUpdate: PaymentAccountCorrectionLinkingUpdate
    ): PaymentAccountCorrectionExtension


    fun createCorrectionExtension(financialDescription: ProjectCorrectionFinancialDescription)

    fun getCorrectionIdsAvailableForPaymentAccounts(fundId: Long): Set<Long>

    /* Overview Summary */

    fun calculateOverviewForDraftPaymentAccount(paymentAccountId: Long): Map<Long?, PaymentAccountAmountSummaryLineTmp>

    fun saveTotalsWhenFinishingPaymentAccount(paymentAccountId: Long, totals: Map<Long?, PaymentAccountAmountSummaryLine>)

    fun getTotalsForFinishedPaymentAccount(paymentAccountId: Long): Map<Long?, PaymentAccountAmountSummaryLine>
}
