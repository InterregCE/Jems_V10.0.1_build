package io.cloudflight.jems.server.payments.service.account.finance.getAmountSummary

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.payments.authorization.CanRetrievePaymentsAccount
import io.cloudflight.jems.server.payments.model.account.PaymentAccountStatus
import io.cloudflight.jems.server.payments.model.account.finance.PaymentAccountAmountSummary
import io.cloudflight.jems.server.payments.model.account.finance.PaymentAccountAmountSummaryLine
import io.cloudflight.jems.server.payments.service.account.PaymentAccountPersistence
import io.cloudflight.jems.server.payments.service.account.finance.PaymentAccountFinancePersistence
import io.cloudflight.jems.server.payments.service.account.finance.computeTotals
import io.cloudflight.jems.server.payments.service.account.finance.correction.PaymentAccountCorrectionLinkingPersistence
import io.cloudflight.jems.server.payments.service.account.finance.correction.plus
import io.cloudflight.jems.server.payments.service.account.finance.correction.sumUp
import io.cloudflight.jems.server.payments.service.account.finance.correction.sumUpProperColumns
import io.cloudflight.jems.server.payments.service.account.finance.mergeBothScoBases
import io.cloudflight.jems.server.payments.service.account.finance.sumUpProperColumns
import io.cloudflight.jems.server.payments.service.ecPayment.PaymentApplicationToEcPersistence
import io.cloudflight.jems.server.payments.service.ecPayment.linkToPayment.PaymentApplicationToEcLinkPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetPaymentAccountAmountSummary(
    private val paymentAccountPersistence: PaymentAccountPersistence,
    private val paymentAccountFinancePersistence: PaymentAccountFinancePersistence,
    private val paymentAccountCorrectionLinkingPersistence: PaymentAccountCorrectionLinkingPersistence,
    private val ecPaymentPersistence: PaymentApplicationToEcPersistence,
    private val ecPaymentCorrectionLinkingPersistence: PaymentApplicationToEcLinkPersistence,
) : GetPaymentAccountAmountSummaryInteractor {

    @CanRetrievePaymentsAccount
    @Transactional
    @ExceptionWrapper(GetPaymentAccountAmountSummaryException::class)
    override fun getSummaryOverview(paymentAccountId: Long): PaymentAccountAmountSummary {
        val paymentAccount = paymentAccountPersistence.getByPaymentAccountId(paymentAccountId)

        val totalsForFinishedEcPayments = getTotalsForFinishedEcPayments(paymentAccount.fund.id, paymentAccount.accountingYear.id)
        val calculatedTotalForDraftEcPayment = getCalculatedTotalForDraftEcPayment(paymentAccount.fund.id, paymentAccount.accountingYear.id)
        val currentOverviewForPaymentAccount = getCurrentOverviewForPaymentAccount(paymentAccount.status, paymentAccount.id)

        val cumulativeOverviewLines = totalsForFinishedEcPayments.plus(calculatedTotalForDraftEcPayment).plus(currentOverviewForPaymentAccount)
        return PaymentAccountAmountSummary(
            amountsGroupedByPriority = cumulativeOverviewLines.values.toList(),
            totals = cumulativeOverviewLines.values.sumUp(),
        )
    }

    private fun getCurrentOverviewForPaymentAccount(paymentAccountStatus: PaymentAccountStatus, paymentAccountId: Long) =
        if (paymentAccountStatus.isFinished())
            paymentAccountCorrectionLinkingPersistence.getTotalsForFinishedPaymentAccount(paymentAccountId)
        else
            paymentAccountCorrectionLinkingPersistence.calculateOverviewForDraftPaymentAccount(paymentAccountId).sumUpProperColumns()

    private fun getTotalsForFinishedEcPayments(fundId: Long, accountingYearId: Long): Map<Long?, PaymentAccountAmountSummaryLine> {
        val finishedEcPaymentIds = ecPaymentPersistence.getFinishedIdsByFundAndAccountingYear(programmeFundId = fundId, accountingYearId = accountingYearId)
        return paymentAccountFinancePersistence.getTotalsForFinishedEcPayments(ecPaymentIds = finishedEcPaymentIds)
            .computeTotals()
    }

    private fun getCalculatedTotalForDraftEcPayment(fundId: Long, accountingYearId: Long): Map<Long?, PaymentAccountAmountSummaryLine> {
        val draftEcPaymentIds = ecPaymentPersistence.getDraftIdsByFundAndAccountingYear(programmeFundId = fundId, accountingYearId = accountingYearId)
        return when (draftEcPaymentIds.size) {
            0 -> emptyMap()
            1 -> ecPaymentCorrectionLinkingPersistence.calculateAndGetOverviewForDraftEcPayment(draftEcPaymentIds.first())
                .sumUpProperColumns()
                .mergeBothScoBases()
            else -> throw EcPaymentDraftNumberExceededException()
        }
    }
}
