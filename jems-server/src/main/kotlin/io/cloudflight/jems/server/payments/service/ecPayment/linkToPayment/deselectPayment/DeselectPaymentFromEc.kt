package io.cloudflight.jems.server.payments.service.ecPayment.linkToPayment.deselectPayment

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.payments.authorization.CanUpdatePaymentApplicationsToEc
import io.cloudflight.jems.server.payments.model.regular.PaymentEcStatus
import io.cloudflight.jems.server.payments.service.ecPayment.linkToPayment.PaymentApplicationToEcLinkPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DeselectPaymentFromEc(
    private val ecPaymentLinkPersistence: PaymentApplicationToEcLinkPersistence,
): DeselectPaymentFromEcInteractor {

    @CanUpdatePaymentApplicationsToEc
    @Transactional
    @ExceptionWrapper(DeselectPaymentFromEcException::class)
    override fun deselectPaymentFromEcPayment(paymentId: Long) {
        val ecPaymentStatus = ecPaymentLinkPersistence.getPaymentExtension(paymentId).ecPaymentStatus
        if (ecPaymentStatus.isFinished())
            throw PaymentApplicationToEcNotInDraftException()

        ecPaymentLinkPersistence.deselectPaymentFromEcPaymentAndResetFields(setOf(paymentId))
    }

    private fun PaymentEcStatus?.isFinished() = this?.isFinished() ?: false

}
