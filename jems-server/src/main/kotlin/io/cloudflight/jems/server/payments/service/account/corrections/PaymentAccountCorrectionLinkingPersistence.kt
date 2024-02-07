package io.cloudflight.jems.server.payments.service.account.corrections

import io.cloudflight.jems.server.payments.model.account.PaymentAccountAmountSummaryLine
import io.cloudflight.jems.server.payments.model.account.PaymentAccountAmountSummaryLineTmp
import io.cloudflight.jems.server.payments.model.account.PaymentAccountCorrectionExtension
import io.cloudflight.jems.server.payments.model.account.PaymentAccountCorrectionLinkingUpdate
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
