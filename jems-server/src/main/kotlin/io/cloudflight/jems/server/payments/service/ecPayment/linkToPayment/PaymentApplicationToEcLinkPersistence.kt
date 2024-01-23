package io.cloudflight.jems.server.payments.service.ecPayment.linkToPayment

import io.cloudflight.jems.server.payments.model.ec.PaymentInEcPaymentMetadata
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcAmountSummaryLine
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcAmountSummaryLineTmp
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcExtension
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcLinkingUpdate
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcOverviewType
import io.cloudflight.jems.server.payments.model.ec.overview.EcPaymentSummaryLine
import io.cloudflight.jems.server.payments.model.regular.PaymentSearchRequestScoBasis

interface PaymentApplicationToEcLinkPersistence {

    fun getPaymentExtension(paymentId: Long): PaymentToEcExtension

    fun getPaymentsLinkedToEcPayment(ecPaymentId: Long): Map<Long, PaymentInEcPaymentMetadata>


    fun selectPaymentToEcPayment(paymentIds: Set<Long>, ecPaymentId: Long)

    fun deselectPaymentFromEcPaymentAndResetFields(paymentIds: Set<Long>)


    fun updatePaymentToEcCorrectedAmounts(paymentId: Long, paymentToEcLinkingUpdate: PaymentToEcLinkingUpdate)

    fun updatePaymentToEcFinalScoBasis(toUpdate: Map<Long, PaymentSearchRequestScoBasis>)


    fun calculateAndGetOverviewForDraftEcPayment(ecPaymentId: Long): Map<PaymentToEcOverviewType, Map<Long?, PaymentToEcAmountSummaryLineTmp>>

    fun saveTotalsWhenFinishingEcPayment(
        ecPaymentId: Long,
        totals: Map<PaymentToEcOverviewType, Map<Long?, PaymentToEcAmountSummaryLine>>,
    )

    fun getTotalsForFinishedEcPayment(
        ecPaymentId: Long,
    ): Map<PaymentToEcOverviewType, Map<Long?, PaymentToEcAmountSummaryLine>>


    fun getCumulativeAmounts(finishedEcPaymentIds: Set<Long>): Map<Long?, EcPaymentSummaryLine>

    fun saveCumulativeAmounts(ecPaymentId: Long, totals: Map<Long?, EcPaymentSummaryLine>)

    fun getCumulativeTotalForEcPayment(ecPaymentId: Long): Map<Long?, PaymentToEcAmountSummaryLine>

    fun getPaymentToEcIdsProjectReportIncluded(projectReportId: Long): Set<Long>

    fun getFtlsIdLinkToEcPaymentIdByProjectId(projectId: Long): Map<Int, Long>

}
