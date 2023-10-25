package io.cloudflight.jems.server.payments.service.ecPayment.linkToPayment.updatePayment

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.payments.authorization.CanUpdatePaymentApplicationsToEc
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcLinkingUpdate
import io.cloudflight.jems.server.payments.model.regular.PaymentEcStatus
import io.cloudflight.jems.server.payments.service.ecPayment.linkToPayment.PaymentApplicationToEcLinkPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdateLinkedPayment(
    private val ecPaymentLinkPersistence: PaymentApplicationToEcLinkPersistence,
) : UpdateLinkedPaymentInteractor {

    @CanUpdatePaymentApplicationsToEc
    @Transactional
    @ExceptionWrapper(UpdateLinkedPaymentException::class)
    override fun updateLinkedPayment(paymentId: Long, updatePaymentForEc: PaymentToEcLinkingUpdate) {
        val ecPaymentStatus = ecPaymentLinkPersistence.getPaymentExtension(paymentId).ecPaymentStatus
        if (ecPaymentStatus.isFinished())
            throw PaymentApplicationToEcNotInDraftException()

        return ecPaymentLinkPersistence.updatePaymentToEcCorrectedAmounts(paymentId, updatePaymentForEc)
    }

    private fun PaymentEcStatus?.isFinished() = this?.isFinished() ?: false

}
