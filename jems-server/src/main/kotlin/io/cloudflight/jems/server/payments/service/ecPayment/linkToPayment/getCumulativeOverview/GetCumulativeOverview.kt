package io.cloudflight.jems.server.payments.service.ecPayment.linkToPayment.getCumulativeOverview

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.payments.authorization.CanRetrievePaymentApplicationsToEc
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcAmountSummary
import io.cloudflight.jems.server.payments.service.ecPayment.PaymentApplicationToEcPersistence
import io.cloudflight.jems.server.payments.service.ecPayment.linkToPayment.PaymentApplicationToEcLinkPersistence
import io.cloudflight.jems.server.payments.service.ecPayment.mergeBothScoBases
import io.cloudflight.jems.server.payments.service.ecPayment.plus
import io.cloudflight.jems.server.payments.service.ecPayment.sumUp
import io.cloudflight.jems.server.payments.service.ecPayment.sumUpProperColumns
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetCumulativeOverview(
    private val ecPaymentPersistence: PaymentApplicationToEcPersistence,
    private val ecPaymentLinkPersistence: PaymentApplicationToEcLinkPersistence,
): GetCumulativeOverviewInteractor {

    @CanRetrievePaymentApplicationsToEc
    @Transactional
    @ExceptionWrapper(GetCumulativeOverviewException::class)
    override fun getCumulativeOverview(ecPaymentId: Long): PaymentToEcAmountSummary {
        val ecPayment = ecPaymentPersistence.getPaymentApplicationToEcDetail(ecPaymentId)

        val cumulativeOverviewForThisEcPayment = ecPaymentLinkPersistence.getCumulativeTotalForEcPayment(ecPaymentId)

        val currentOverview = (if (ecPayment.status.isFinished())
            ecPaymentLinkPersistence.getTotalsForFinishedEcPayment(ecPaymentId)
        else
            ecPaymentLinkPersistence.calculateAndGetOverviewForDraftEcPayment(ecPaymentId).sumUpProperColumns()
        ).mergeBothScoBases()

        val cumulativeOverviewLines = currentOverview.plus(cumulativeOverviewForThisEcPayment)
        return PaymentToEcAmountSummary(
            amountsGroupedByPriority = cumulativeOverviewLines.values.toList(),
            totals = cumulativeOverviewLines.values.sumUp(),
        )

    }

}
