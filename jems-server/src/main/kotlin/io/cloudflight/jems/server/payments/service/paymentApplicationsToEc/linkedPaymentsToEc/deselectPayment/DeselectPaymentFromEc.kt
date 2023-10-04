package io.cloudflight.jems.server.payments.service.paymentApplicationsToEc.linkedPaymentsToEc.deselectPayment

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.payments.authorization.CanUpdatePaymentApplicationsToEc
import io.cloudflight.jems.server.payments.model.regular.PaymentEcStatus
import io.cloudflight.jems.server.payments.service.paymentApplicationsToEc.PaymentApplicationToEcPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DeselectPaymentFromEc(
    private val paymentApplicationsToEcPersistence: PaymentApplicationToEcPersistence,
): DeselectPaymentFromEcInteractor {

    @CanUpdatePaymentApplicationsToEc
    @Transactional
    @ExceptionWrapper(DeselectPaymentFromEcException::class)
    override fun deselectPaymentFromEcPayment(paymentId: Long) {
        val paymentExtension = paymentApplicationsToEcPersistence.getPaymentExtension(paymentId)
        if (paymentExtension.ecPaymentStatus != PaymentEcStatus.Draft)
            throw PaymentApplicationToEcNotInDraftException()

        paymentApplicationsToEcPersistence.deselectPaymentFromEcPaymentAndResetFields(paymentId)
    }

}
