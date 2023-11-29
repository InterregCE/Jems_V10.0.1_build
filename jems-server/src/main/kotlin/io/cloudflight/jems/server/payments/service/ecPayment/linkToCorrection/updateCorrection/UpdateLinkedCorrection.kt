package io.cloudflight.jems.server.payments.service.ecPayment.linkToCorrection.updateCorrection

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.payments.authorization.CanUpdatePaymentApplicationsToEc
import io.cloudflight.jems.server.payments.model.ec.EcPaymentCorrectionExtension
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcCorrectionLinkingUpdate
import io.cloudflight.jems.server.payments.model.regular.PaymentEcStatus
import io.cloudflight.jems.server.payments.service.ecPayment.linkToCorrection.EcPaymentCorrectionLinkPersistence
import io.cloudflight.jems.server.payments.service.ecPayment.linkToPayment.updatePayment.PaymentApplicationToEcNotInDraftException
import io.cloudflight.jems.server.payments.service.ecPayment.linkToPayment.updatePayment.UpdateLinkedCorrectionException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdateLinkedCorrection(
    private val ecPaymentCorrectionLinkPersistence: EcPaymentCorrectionLinkPersistence,
) : UpdateLinkedCorrectionInteractor {

    @CanUpdatePaymentApplicationsToEc
    @Transactional
    @ExceptionWrapper(UpdateLinkedCorrectionException::class)
    override fun updateLinkedCorrection(
        correctionId: Long,
        updateLinkedCorrection: PaymentToEcCorrectionLinkingUpdate
    ): EcPaymentCorrectionExtension {
        val ecPaymentStatus = ecPaymentCorrectionLinkPersistence.getCorrectionExtension(correctionId).ecPaymentStatus
        if (ecPaymentStatus.isFinished())
            throw PaymentApplicationToEcNotInDraftException()

        return ecPaymentCorrectionLinkPersistence.updateCorrectionLinkedToEcPaymentCorrectedAmounts(
            correctionId,
            updateLinkedCorrection
        )
    }

    private fun PaymentEcStatus?.isFinished() = this?.isFinished() ?: false

}
