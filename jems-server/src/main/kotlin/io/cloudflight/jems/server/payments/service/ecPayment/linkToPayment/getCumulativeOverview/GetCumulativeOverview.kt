package io.cloudflight.jems.server.payments.service.ecPayment.linkToPayment.getCumulativeOverview

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.payments.authorization.CanRetrievePaymentApplicationsToEc
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcAmountSummary
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcAmountSummaryLine
import io.cloudflight.jems.server.payments.service.ecPayment.linkToPayment.PaymentApplicationToEcLinkPersistence
import io.cloudflight.jems.server.payments.service.ecPayment.merge
import io.cloudflight.jems.server.payments.service.ecPayment.sumUp
import io.cloudflight.jems.server.payments.service.ecPayment.sumUpProperColumns
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
class GetCumulativeOverview(
    private val ecPaymentLinkPersistence: PaymentApplicationToEcLinkPersistence,
): GetCumulativeOverviewInteractor {

    companion object {
        private val ZERO_SUMMARY_LINE = PaymentToEcAmountSummaryLine(
            priorityAxis = null,
            totalEligibleExpenditure = BigDecimal.ZERO,
            totalUnionContribution = BigDecimal.ZERO,
            totalPublicContribution = BigDecimal.ZERO,
        )
    }

    @CanRetrievePaymentApplicationsToEc
    @Transactional
    @ExceptionWrapper(GetCumulativeOverviewException::class)
    override fun getCumulativeOverview(ecPaymentId: Long): PaymentToEcAmountSummary {

        val currentOverview = ecPaymentLinkPersistence.calculateAndGetOverview(ecPaymentId).sumUpProperColumns().merge()
        val cumulativeOverviewForFinishedEcPayments = ecPaymentLinkPersistence.getCumulativeTotalForEcPayment(ecPaymentId)
            .associateBy { it.priorityAxis!! }

        val cumulativeOverviewLines = currentOverview.addPreviouslyFinishedCumulativeAmounts(cumulativeOverviewForFinishedEcPayments)
        return PaymentToEcAmountSummary(
            amountsGroupedByPriority = cumulativeOverviewLines,
            totals = cumulativeOverviewLines.sumUp()
        )

    }


    fun List<PaymentToEcAmountSummaryLine>.addPreviouslyFinishedCumulativeAmounts(
        cumulativeAmounts: Map<String, PaymentToEcAmountSummaryLine>
    ): List<PaymentToEcAmountSummaryLine> =

         this.map { current ->
             val cumulativeValue = cumulativeAmounts.getOrDefault(current.priorityAxis, ZERO_SUMMARY_LINE)
             current.plus(cumulativeValue)
         }



    fun PaymentToEcAmountSummaryLine.plus(other: PaymentToEcAmountSummaryLine): PaymentToEcAmountSummaryLine =
        PaymentToEcAmountSummaryLine(
            priorityAxis = this.priorityAxis,
            totalEligibleExpenditure = this.totalEligibleExpenditure.plus(other.totalEligibleExpenditure),
            totalUnionContribution = this.totalUnionContribution.plus(other.totalUnionContribution),
            totalPublicContribution = this.totalPublicContribution.plus(other.totalPublicContribution)
        )

}