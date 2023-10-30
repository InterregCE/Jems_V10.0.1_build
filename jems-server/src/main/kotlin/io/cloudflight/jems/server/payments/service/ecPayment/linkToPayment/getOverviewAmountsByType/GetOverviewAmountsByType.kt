package io.cloudflight.jems.server.payments.service.ecPayment.linkToPayment.getOverviewAmountsByType

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.payments.authorization.CanRetrievePaymentApplicationsToEc
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcAmountSummary
import io.cloudflight.jems.server.payments.model.regular.PaymentEcStatus
import io.cloudflight.jems.server.payments.model.regular.PaymentSearchRequestScoBasis
import io.cloudflight.jems.server.payments.service.ecPayment.PaymentApplicationToEcPersistence
import io.cloudflight.jems.server.payments.service.ecPayment.linkToPayment.PaymentApplicationToEcLinkPersistence
import io.cloudflight.jems.server.payments.service.ecPayment.linkToPayment.getCumulativeAmountsForArtNot94Not95.GetOverviewByTypeInteractor
import io.cloudflight.jems.server.payments.service.ecPayment.merge
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
    override fun getOverviewAmountsByType(paymentToEcId: Long, type: PaymentSearchRequestScoBasis?): PaymentToEcAmountSummary {
        val ecPayment = ecPaymentPersistence.getPaymentApplicationToEcDetail(paymentToEcId)

        val selectedPaymentList = if (ecPayment.status == PaymentEcStatus.Finished)
                ecPaymentLinkPersistence.getTotalsForFinishedEcPayment(paymentToEcId)
            else
                ecPaymentLinkPersistence.calculateAndGetOverview(paymentToEcId).sumUpProperColumns()

        val selectedPaymentListOfType = if (type != null) selectedPaymentList[type]!! else selectedPaymentList.merge()

        return PaymentToEcAmountSummary(
            amountsGroupedByPriority = selectedPaymentListOfType,
            totals = selectedPaymentListOfType.sumUp()
        )
    }
}
