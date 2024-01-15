package io.cloudflight.jems.server.payments.service.ecPayment.linkToCorrection.updateCorrection

import io.cloudflight.jems.server.payments.model.ec.EcPaymentCorrectionExtension
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcCorrectionLinkingUpdate

interface UpdateLinkedCorrectionInteractor {

    fun updateLinkedCorrection(
        correctionId: Long,
        updateLinkedCorrection: PaymentToEcCorrectionLinkingUpdate
    ): EcPaymentCorrectionExtension

}
