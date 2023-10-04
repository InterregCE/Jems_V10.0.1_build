package io.cloudflight.jems.server.payments.service.paymentApplicationsToEc.linkedPaymentsToEc.updatePayment

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.payments.authorization.CanUpdatePaymentApplicationsToEc
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcLinkingUpdate
import io.cloudflight.jems.server.payments.model.regular.PaymentEcStatus
import io.cloudflight.jems.server.payments.service.paymentApplicationsToEc.PaymentApplicationToEcPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdateLinkedPayment(
    private val paymentApplicationsToEcPersistence: PaymentApplicationToEcPersistence,
) : UpdateLinkedPaymentInteractor {

    @CanUpdatePaymentApplicationsToEc
    @Transactional
    @ExceptionWrapper(UpdateLinkedPaymentException::class)
    override fun updateLinkedPayment(paymentId: Long, updatePaymentForEc: PaymentToEcLinkingUpdate) {
        val paymentExtension = paymentApplicationsToEcPersistence.getPaymentExtension(paymentId)
        if (paymentExtension.ecPaymentStatus == PaymentEcStatus.Finished)
            throw PaymentApplicationToEcNotInDraftException()

        return paymentApplicationsToEcPersistence.updatePaymentToEcCorrectedAmounts(paymentId, updatePaymentForEc)
    }


}
