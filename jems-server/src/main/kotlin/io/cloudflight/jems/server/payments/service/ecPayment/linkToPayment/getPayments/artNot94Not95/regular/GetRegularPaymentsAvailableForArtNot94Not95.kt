package io.cloudflight.jems.server.payments.service.ecPayment.linkToPayment.getPayments.artNot94Not95.regular

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.payments.authorization.CanRetrievePaymentApplicationsToEc
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcPayment
import io.cloudflight.jems.server.payments.model.regular.PaymentSearchRequestScoBasis
import io.cloudflight.jems.server.payments.model.regular.PaymentSearchRequestScoBasis.DoesNotFallUnderArticle94Nor95
import io.cloudflight.jems.server.payments.model.regular.PaymentType
import io.cloudflight.jems.server.payments.service.ecPayment.PaymentApplicationToEcPersistence
import io.cloudflight.jems.server.payments.service.ecPayment.constructFilter
import io.cloudflight.jems.server.payments.service.regular.PaymentPersistence
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetRegularPaymentsAvailableForArtNot94Not95(
    private val ecPaymentPersistence: PaymentApplicationToEcPersistence,
    private val paymentPersistence: PaymentPersistence
) : GetRegularPaymentsAvailableForArtNot94Not95Interactor {

    @CanRetrievePaymentApplicationsToEc
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetRegularPaymentsAvailableForArtNot94Not95Exception::class)
    override fun getPaymentList(pageable: Pageable, ecPaymentId: Long): Page<PaymentToEcPayment> {
        val ecPayment = ecPaymentPersistence.getPaymentApplicationToEcDetail(ecPaymentId)
        val fundId = ecPayment.paymentApplicationToEcSummary.programmeFund.id

        val filter = if (ecPayment.status.isFinished())
            filterRegular(ecPaymentId.nonNull(), finalScoBasis = DoesNotFallUnderArticle94Nor95)
        else
            filterRegular(ecPaymentId.orNull(), fundId = fundId, contractingScoBasis = DoesNotFallUnderArticle94Nor95)

        return paymentPersistence.getAllPaymentToEcPayment(pageable, filter)
    }

    private fun Long.orNull() = setOf(this, null)
    private fun Long.nonNull() = setOf(this)

    private fun filterRegular(
        ecPaymentIds: Set<Long?>,
        fundId: Long? = null,
        finalScoBasis: PaymentSearchRequestScoBasis? = null,
        contractingScoBasis: PaymentSearchRequestScoBasis? = null,
    ) = constructFilter(
        ecPaymentIds = ecPaymentIds,
        fundId = fundId,
        contractingScoBasis = contractingScoBasis,
        finalScoBasis = finalScoBasis,
        paymentType = PaymentType.REGULAR,
    )

}
