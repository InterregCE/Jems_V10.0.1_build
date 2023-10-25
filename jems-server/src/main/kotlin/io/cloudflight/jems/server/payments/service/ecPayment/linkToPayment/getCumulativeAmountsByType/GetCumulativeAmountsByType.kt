package io.cloudflight.jems.server.payments.service.ecPayment.linkToPayment.getCumulativeAmountsByType

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.payments.authorization.CanRetrievePaymentApplicationsToEc
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcAmountSummary
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcAmountSummaryLine
import io.cloudflight.jems.server.payments.model.regular.*
import io.cloudflight.jems.server.payments.service.ecPayment.PaymentApplicationToEcPersistence
import io.cloudflight.jems.server.payments.service.ecPayment.linkToPayment.PaymentApplicationToEcLinkPersistence
import io.cloudflight.jems.server.payments.service.ecPayment.linkToPayment.getCumulativeAmountsForArtNot94Not95.GetCumulativeAmountsByTypeInteractor
import io.cloudflight.jems.server.payments.service.ecPayment.sumUpProperColumns
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
class GetCumulativeAmountsByType(
    private val ecPaymentPersistence: PaymentApplicationToEcPersistence,
    private val ecPaymentLinkPersistence: PaymentApplicationToEcLinkPersistence,
) : GetCumulativeAmountsByTypeInteractor {

    @CanRetrievePaymentApplicationsToEc
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetCumulativeAmountsByTypeException::class)
    override fun getCumulativeAmountsByType(paymentToEcId: Long, type: PaymentSearchRequestScoBasis?): PaymentToEcAmountSummary {
        val ecPayment = ecPaymentPersistence.getPaymentApplicationToEcDetail(paymentToEcId)

        val selectedPaymentList = if (ecPayment.status == PaymentEcStatus.Finished)
                ecPaymentLinkPersistence.getTotalsForFinishedEcPayment(paymentToEcId)
            else
                ecPaymentLinkPersistence.calculateAndGetTotals(paymentToEcId).sumUpProperColumns()

        val selectedPaymentListOfType = if (type != null) selectedPaymentList[type]!! else selectedPaymentList.merge()

        return PaymentToEcAmountSummary(
            amountsGroupedByPriority = selectedPaymentListOfType,
            totals = selectedPaymentListOfType.sumUp()
        )
    }

    private fun Collection<PaymentToEcAmountSummaryLine>.sumUp() = PaymentToEcAmountSummaryLine (
        priorityAxis = if (allAxesSame()) firstOrNull()?.priorityAxis else null,
        totalEligibleExpenditure = sumOf { it.totalEligibleExpenditure },
        totalUnionContribution = BigDecimal.ZERO,
        totalPublicContribution = sumOf { it.totalPublicContribution }
    )

    private fun Map<PaymentSearchRequestScoBasis, List<PaymentToEcAmountSummaryLine>>.merge(): List<PaymentToEcAmountSummaryLine> =
        values
            .flatten()
            .groupBy { it.priorityAxis }
            .mapValues { (_, values) -> values.sumUp() }
            .values.toList()

    private fun Collection<PaymentToEcAmountSummaryLine>.allAxesSame() = mapTo(HashSet()) { it.priorityAxis }.size == 1

}
