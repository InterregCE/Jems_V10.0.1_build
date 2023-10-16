package io.cloudflight.jems.server.payments.service.paymentApplicationsToEc.linkedPaymentsToEc.getPayments.regular.artNot94Not95

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.payments.authorization.CanRetrievePaymentApplicationsToEc
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcPayment
import io.cloudflight.jems.server.payments.model.regular.PaymentEcStatus
import io.cloudflight.jems.server.payments.model.regular.PaymentSearchRequest
import io.cloudflight.jems.server.payments.model.regular.PaymentSearchRequestScoBasis
import io.cloudflight.jems.server.payments.model.regular.PaymentType
import io.cloudflight.jems.server.payments.service.paymentApplicationsToEc.PaymentApplicationToEcPersistence
import io.cloudflight.jems.server.payments.service.regular.PaymentPersistence
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetRegularPaymentsAvailableForArtNot94Not95(
    private val paymentToEcPersistence: PaymentApplicationToEcPersistence,
    private val paymentPersistence: PaymentPersistence
) : GetRegularPaymentsAvailableForArtNot94Not95Interactor {

    @CanRetrievePaymentApplicationsToEc
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetRegularPaymentsAvailableForArtNot94Not95Exception::class)
    override fun getPaymentList(pageable: Pageable, ecApplicationId: Long): Page<PaymentToEcPayment> {
        val ecPayment = paymentToEcPersistence.getPaymentApplicationToEcDetail(ecApplicationId)

        val filter = constructFilter(
            ecApplicationId = ecPayment.id,
            fundId = ecPayment.paymentApplicationToEcSummary.programmeFund.id,
            scoBasis = PaymentSearchRequestScoBasis.DoesNotFallUnderArticle94Nor95,
            paymentType = PaymentType.REGULAR,
            ecStatus = ecPayment.status
        )

        return paymentPersistence.getAllPaymentToEcPayment(pageable, filter)
    }

    private fun constructFilter(
        ecApplicationId: Long,
        fundId: Long,
        scoBasis: PaymentSearchRequestScoBasis,
        paymentType: PaymentType,
        ecStatus: PaymentEcStatus
    ) = PaymentSearchRequest(
        paymentId = null,
        paymentType = paymentType,
        projectIdentifiers = emptySet(),
        projectAcronym = null,
        claimSubmissionDateFrom = null,
        claimSubmissionDateTo = null,
        approvalDateFrom = null,
        approvalDateTo = null,
        fundIds = setOf(fundId),
        lastPaymentDateFrom = null,
        lastPaymentDateTo = null,
        availableForEcId = ecApplicationId,
        scoBasis = scoBasis,
        ecStatus = ecStatus
    )
}
