package io.cloudflight.jems.server.payments.service.paymentApplicationsToEc.linkedPaymentsToEc.getPayments.artNot94Not95.ftls

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.payments.authorization.CanRetrievePaymentApplicationsToEc
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcPayment
import io.cloudflight.jems.server.payments.model.regular.PaymentSearchRequestScoBasis
import io.cloudflight.jems.server.payments.model.regular.PaymentType
import io.cloudflight.jems.server.payments.service.paymentApplicationsToEc.PaymentApplicationToEcPersistence
import io.cloudflight.jems.server.payments.service.paymentApplicationsToEc.constructFilter
import io.cloudflight.jems.server.payments.service.regular.PaymentPersistence
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetFtlsPaymentsAvailableForArtNot94Not95(
    private val paymentToEcPersistence: PaymentApplicationToEcPersistence,
    private val paymentPersistence: PaymentPersistence,
) : GetFtlsPaymentsAvailableForArtNot94Not95Interactor {

    @CanRetrievePaymentApplicationsToEc
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetFtlsPaymentsAvailableForArtNot94Not95Exception::class)
    override fun getPaymentList(pageable: Pageable, ecApplicationId: Long): Page<PaymentToEcPayment> {
        val ecPayment = paymentToEcPersistence.getPaymentApplicationToEcDetail(ecApplicationId)

        val filter = constructFilter(
            ecApplicationId = ecPayment.id,
            fundId = ecPayment.paymentApplicationToEcSummary.programmeFund.id,
            scoBasis = PaymentSearchRequestScoBasis.DoesNotFallUnderArticle94Nor95,
            paymentType = PaymentType.FTLS,
        )

        return paymentPersistence.getAllPaymentToEcPayment(pageable, filter)
    }

}
