package io.cloudflight.jems.server.payments.service.ecPayment.linkToPayment.getPayments.artNot94Not95

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.payments.authorization.CanRetrievePaymentApplicationsToEc
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEcDetail
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcPayment
import io.cloudflight.jems.server.payments.model.regular.PaymentSearchRequest
import io.cloudflight.jems.server.payments.model.regular.PaymentSearchRequestScoBasis
import io.cloudflight.jems.server.payments.model.regular.PaymentType
import io.cloudflight.jems.server.payments.service.ecPayment.PaymentApplicationToEcPersistence
import io.cloudflight.jems.server.payments.service.ecPayment.constructFilter
import io.cloudflight.jems.server.payments.service.regular.PaymentPersistence
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


@Service
class GetPaymentsAvailableArtNot94Not95(
    private val ecPaymentPersistence: PaymentApplicationToEcPersistence,
    private val paymentPersistence: PaymentPersistence
): GetPaymentsAvailableArtNot94Not95Interactor {

    @CanRetrievePaymentApplicationsToEc
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetPaymentsAvailableNotArt94Not95Exception::class)
    override fun getPaymentList(
        pageable: Pageable,
        ecPaymentId: Long,
        paymentType: PaymentType
    ): Page<PaymentToEcPayment> {
        val ecPayment = ecPaymentPersistence.getPaymentApplicationToEcDetail(ecPaymentId)
        val filter = constructPaymentFilter(ecPayment, paymentType)
        return paymentPersistence.getAllPaymentToEcPayment(pageable, filter)
    }

    private fun constructPaymentFilter(ecPayment: PaymentApplicationToEcDetail, paymentType: PaymentType): PaymentSearchRequest {
        val fundId = ecPayment.paymentApplicationToEcSummary.programmeFund.id

        return if (ecPayment.status.isFinished()) {
            constructFilter(
                ecPaymentIds = ecPayment.id.asSet(),
                fundId = fundId,
                contractingScoBasis = null,
                finalScoBasis = PaymentSearchRequestScoBasis.DoesNotFallUnderArticle94Nor95,
                paymentType = paymentType,
            )
        } else {
            constructFilter(
                ecPaymentIds = ecPayment.id.orNull(),
                fundId = fundId,
                contractingScoBasis = PaymentSearchRequestScoBasis.DoesNotFallUnderArticle94Nor95,
                finalScoBasis = null,
                paymentType = paymentType,
            )
        }
    }

    private fun Long.orNull() = setOf(this, null)
    private fun Long.asSet() = setOf(this)
}
