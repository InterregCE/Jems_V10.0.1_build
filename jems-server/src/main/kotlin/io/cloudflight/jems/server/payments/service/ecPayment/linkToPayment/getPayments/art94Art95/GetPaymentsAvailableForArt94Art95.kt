package io.cloudflight.jems.server.payments.service.ecPayment.linkToPayment.getPayments.art94Art95

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.payments.authorization.CanRetrievePaymentApplicationsToEc
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcPayment
import io.cloudflight.jems.server.payments.model.regular.PaymentSearchRequestScoBasis.FallsUnderArticle94Or95
import io.cloudflight.jems.server.payments.model.regular.PaymentType
import io.cloudflight.jems.server.payments.service.ecPayment.PaymentApplicationToEcPersistence
import io.cloudflight.jems.server.payments.service.ecPayment.linkToPayment.getPayments.constructFilter
import io.cloudflight.jems.server.payments.service.regular.PaymentPersistence
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetPaymentsAvailableForArt94Art95(
    private val ecPaymentPersistence: PaymentApplicationToEcPersistence,
    private val paymentPersistence: PaymentPersistence
): GetPaymentsAvailableForArt94Art95Interactor {

    @CanRetrievePaymentApplicationsToEc
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetPaymentsAvailableForArt94Art95Exception::class)
    override fun getPaymentList(
        pageable: Pageable,
        ecPaymentId: Long,
        paymentType: PaymentType
    ): Page<PaymentToEcPayment> {
        val ecPayment = ecPaymentPersistence.getPaymentApplicationToEcDetail(ecPaymentId)
        val fundId = ecPayment.paymentApplicationToEcSummary.programmeFund.id

        val filter = if (ecPayment.status.isFinished())
            constructFilter(paymentType, ecPaymentId.asSet(), finalScoBasis = FallsUnderArticle94Or95)
        else
            constructFilter(paymentType, ecPaymentId.orNull(), fundId = fundId, contractingScoBasis = FallsUnderArticle94Or95)

        return paymentPersistence.getAllPaymentToEcPayment(pageable, filter)
    }

    private fun Long.orNull() = setOf(this, null)
    private fun Long.asSet() = setOf(this)

}
