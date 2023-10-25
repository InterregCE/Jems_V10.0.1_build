package io.cloudflight.jems.server.payments.service.ecPayment.deletePaymentApplicationToEc

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.payments.authorization.CanUpdatePaymentApplicationsToEc
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEcDetail
import io.cloudflight.jems.server.payments.model.regular.PaymentSearchRequestScoBasis
import io.cloudflight.jems.server.payments.model.regular.PaymentToProject
import io.cloudflight.jems.server.payments.service.paymentApplicationToEcDeleted
import io.cloudflight.jems.server.payments.service.ecPayment.PaymentApplicationToEcPersistence
import io.cloudflight.jems.server.payments.service.ecPayment.constructFilter
import io.cloudflight.jems.server.payments.service.ecPayment.linkToPayment.PaymentApplicationToEcLinkPersistence
import io.cloudflight.jems.server.payments.service.regular.PaymentPersistence
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DeletePaymentApplicationToEc(
    private val ecPaymentPersistence: PaymentApplicationToEcPersistence,
    private val ecPaymentLinkPersistence: PaymentApplicationToEcLinkPersistence,
    private val paymentPersistence: PaymentPersistence,
    private val auditPublisher: ApplicationEventPublisher
) : DeletePaymentApplicationToEcInteractor {

    @CanUpdatePaymentApplicationsToEc
    @Transactional
    @ExceptionWrapper(DeletePaymentApplicationToEcException::class)
    override fun deleteById(id: Long) {
        val ecPayment = ecPaymentPersistence.getPaymentApplicationToEcDetail(id)
        if (ecPayment.status.isFinished())
            throw PaymentFinishedException()

        val toResetPaymentIds = paymentPersistence
            .getAllPaymentToProject(Pageable.unpaged(), onlyPaymentsOfThisEcPayment(ecPayment))
            .onlyIds()
        ecPaymentLinkPersistence.deselectPaymentFromEcPaymentAndResetFields(toResetPaymentIds)

        ecPaymentPersistence.deleteById(id).also {
            auditPublisher.publishEvent(
                paymentApplicationToEcDeleted(context = this, ecPayment)
            )
        }
    }

    private fun onlyPaymentsOfThisEcPayment(ecPayment: PaymentApplicationToEcDetail) = constructFilter(
        ecPaymentIds = setOf(ecPayment.id),
        scoBasis = PaymentSearchRequestScoBasis.DoesNotFallUnderArticle94Nor95,
    )

    private fun Page<PaymentToProject>.onlyIds() = content.mapTo(HashSet()) { it.id }

}
