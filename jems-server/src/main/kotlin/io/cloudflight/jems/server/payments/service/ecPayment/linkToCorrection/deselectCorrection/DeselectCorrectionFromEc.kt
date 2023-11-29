package io.cloudflight.jems.server.payments.service.ecPayment.linkToCorrection.deselectCorrection

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.payments.authorization.CanUpdatePaymentApplicationsToEc
import io.cloudflight.jems.server.payments.model.regular.PaymentEcStatus
import io.cloudflight.jems.server.payments.service.ecPayment.linkToCorrection.EcPaymentCorrectionLinkPersistence
import io.cloudflight.jems.server.payments.service.ecPayment.linkToPayment.deselectPayment.PaymentApplicationToEcNotInDraftException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DeselectCorrectionFromEc(
    private val ecPaymentCorrectionLinkPersistence: EcPaymentCorrectionLinkPersistence
): DeselectCorrectionFromEcInteractor {

    @CanUpdatePaymentApplicationsToEc
    @Transactional
    @ExceptionWrapper(DeselectCorrectionFromEcException::class)
    override fun deselectCorrectionFromEcPayment(correctionId: Long) {
        val ecPaymentStatus = ecPaymentCorrectionLinkPersistence.getCorrectionExtension(correctionId).ecPaymentStatus
        if (ecPaymentStatus.isFinished())
            throw PaymentApplicationToEcNotInDraftException()

        ecPaymentCorrectionLinkPersistence.deselectCorrectionFromEcPaymentAndResetFields(setOf(correctionId))
    }

    private fun PaymentEcStatus?.isFinished() = this?.isFinished() ?: false

}
