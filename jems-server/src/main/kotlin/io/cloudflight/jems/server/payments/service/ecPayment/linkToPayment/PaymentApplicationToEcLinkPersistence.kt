package io.cloudflight.jems.server.payments.service.ecPayment.linkToPayment

import io.cloudflight.jems.server.payments.model.ec.PaymentInEcPaymentMetadata
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcAmountSummaryLine
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcAmountSummaryLineTmp
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcExtension
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcLinkingUpdate
import io.cloudflight.jems.server.payments.model.regular.PaymentSearchRequestScoBasis

interface PaymentApplicationToEcLinkPersistence {

    fun getPaymentExtension(paymentId: Long): PaymentToEcExtension

    fun getPaymentsLinkedToEcPayment(ecPaymentId: Long): Map<Long, PaymentInEcPaymentMetadata>


    fun selectPaymentToEcPayment(paymentIds: Set<Long>, ecPaymentId: Long)

    fun deselectPaymentFromEcPaymentAndResetFields(paymentIds: Set<Long>)


    fun updatePaymentToEcCorrectedAmounts(paymentId: Long, paymentToEcLinkingUpdate: PaymentToEcLinkingUpdate)

    fun updatePaymentToEcFinalScoBasis(toUpdate: Map<Long, PaymentSearchRequestScoBasis>)


    fun calculateAndGetOverview(ecPaymentId: Long): Map<PaymentSearchRequestScoBasis, List<PaymentToEcAmountSummaryLineTmp>>

    fun saveTotalsWhenFinishingEcPayment(
        ecPaymentId: Long,
        totals: Map<PaymentSearchRequestScoBasis, List<PaymentToEcAmountSummaryLine>>,
    )

    fun getTotalsForFinishedEcPayment(
        ecPaymentId: Long,
    ): Map<PaymentSearchRequestScoBasis, List<PaymentToEcAmountSummaryLine>>


    fun getCumulativeOverviewForFundAndYear(
        fundId: Long,
        accountingYearId: Long
    ): List<PaymentToEcAmountSummaryLine>


    fun saveCumulativeAmounts(ecPaymentId: Long, totals: List<PaymentToEcAmountSummaryLine>)


    fun getCumulativeTotalForEcPayment(ecPaymentId: Long,): List<PaymentToEcAmountSummaryLine>

}
