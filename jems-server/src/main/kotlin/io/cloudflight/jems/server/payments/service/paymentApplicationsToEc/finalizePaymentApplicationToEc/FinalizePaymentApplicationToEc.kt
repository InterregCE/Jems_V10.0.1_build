package io.cloudflight.jems.server.payments.service.paymentApplicationsToEc.finalizePaymentApplicationToEc

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.payments.authorization.CanUpdatePaymentApplicationsToEc
import io.cloudflight.jems.server.payments.model.regular.PaymentEcStatus
import io.cloudflight.jems.server.payments.service.paymentApplicationToEcFinalized
import io.cloudflight.jems.server.payments.service.paymentApplicationsToEc.PaymentApplicationToEcPersistence
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class FinalizePaymentApplicationToEc(
    private val auditPublisher: ApplicationEventPublisher,
    private val paymentApplicationsToEcPersistence: PaymentApplicationToEcPersistence,
) : FinalizePaymentApplicationToEcInteractor {

    @CanUpdatePaymentApplicationsToEc
    @Transactional
    @ExceptionWrapper(FinalizePaymentApplicationToEcException::class)
    override fun finalizePaymentApplicationToEc(paymentId: Long): PaymentEcStatus {
        validatePaymentApplicationIsDraft(paymentId)

        return paymentApplicationsToEcPersistence.finalizePaymentApplicationToEc(paymentId).also {
            auditPublisher.publishEvent(paymentApplicationToEcFinalized(
                context = this,
                paymentApplicationToEc = it,
                paymentApplicationsToEcPersistence.getPaymentsLinkedToEcPayment(paymentId),
            ))
        }.status
    }

    private fun validatePaymentApplicationIsDraft(paymentId: Long) {
        val paymentApplication = paymentApplicationsToEcPersistence.getPaymentApplicationToEcDetail(paymentId)

        if (paymentApplication.status != PaymentEcStatus.Draft)
            throw PaymentApplicationToEcNotInDraftException()
    }
}
