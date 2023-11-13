package io.cloudflight.jems.server.payments.service.ecPayment.linkToPayment.getOverviewAmountsByType

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.payments.authorization.CanRetrievePaymentApplicationsToEc
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcAmountSummary
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcOverviewType
import io.cloudflight.jems.server.payments.service.ecPayment.PaymentApplicationToEcPersistence
import io.cloudflight.jems.server.payments.service.ecPayment.linkToPayment.PaymentApplicationToEcLinkPersistence
import io.cloudflight.jems.server.payments.service.ecPayment.linkToPayment.getCumulativeAmountsForArtNot94Not95.GetOverviewByTypeInteractor
import io.cloudflight.jems.server.payments.service.ecPayment.mergeBothScoBases
import io.cloudflight.jems.server.payments.service.ecPayment.sumUp
import io.cloudflight.jems.server.payments.service.ecPayment.sumUpProperColumns
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetOverviewAmountsByType(
    private val ecPaymentPersistence: PaymentApplicationToEcPersistence,
    private val ecPaymentLinkPersistence: PaymentApplicationToEcLinkPersistence,
) : GetOverviewByTypeInteractor {

    @CanRetrievePaymentApplicationsToEc
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetOverviewAmountsByTypeException::class)
    override fun getOverviewAmountsByType(paymentToEcId: Long, type: PaymentToEcOverviewType?): PaymentToEcAmountSummary {
        val ecPayment = ecPaymentPersistence.getPaymentApplicationToEcDetail(paymentToEcId)

        val currentOverview = if (ecPayment.status.isFinished())
                ecPaymentLinkPersistence.getTotalsForFinishedEcPayment(paymentToEcId)
            else
                ecPaymentLinkPersistence.calculateAndGetOverview(paymentToEcId).sumUpProperColumns()

        val currentOverviewOfType = if (type != null) currentOverview[type]!! else currentOverview.mergeBothScoBases()

        return PaymentToEcAmountSummary(
            amountsGroupedByPriority = currentOverviewOfType.values.toList(),
            totals = currentOverviewOfType.values.sumUp()
        )
    }
}
