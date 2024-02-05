package io.cloudflight.jems.server.payments.service.ecPayment.linkToCorrection.updateCorrection

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.payments.authorization.CanUpdatePaymentApplicationsToEc
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcCorrectionExtension
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcCorrectionLinkingUpdate
import io.cloudflight.jems.server.payments.model.regular.PaymentEcStatus
import io.cloudflight.jems.server.payments.service.ecPayment.linkToCorrection.EcPaymentCorrectionLinkPersistence
import io.cloudflight.jems.server.payments.service.ecPayment.linkToPayment.updatePayment.PaymentApplicationToEcNotInDraftException
import io.cloudflight.jems.server.payments.service.ecPayment.linkToPayment.updatePayment.UpdateLinkedCorrectionToEcPaymentException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdateLinkedCorrectionToEcPayment(
    private val ecPaymentCorrectionLinkPersistence: EcPaymentCorrectionLinkPersistence,
) : UpdateLinkedCorrectionToEcPaymentInteractor {

    @CanUpdatePaymentApplicationsToEc
    @Transactional
    @ExceptionWrapper(UpdateLinkedCorrectionToEcPaymentException::class)
    override fun updateLinkedCorrection(
        correctionId: Long,
        updateLinkedCorrection: PaymentToEcCorrectionLinkingUpdate
    ): PaymentToEcCorrectionExtension {
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
