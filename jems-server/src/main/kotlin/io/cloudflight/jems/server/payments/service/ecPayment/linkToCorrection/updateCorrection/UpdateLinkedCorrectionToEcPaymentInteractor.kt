package io.cloudflight.jems.server.payments.service.ecPayment.linkToCorrection.updateCorrection

import io.cloudflight.jems.server.payments.model.ec.PaymentToEcCorrectionExtension
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcCorrectionLinkingUpdate

interface UpdateLinkedCorrectionToEcPaymentInteractor {

    fun updateLinkedCorrection(
        correctionId: Long,
        updateLinkedCorrection: PaymentToEcCorrectionLinkingUpdate
    ): PaymentToEcCorrectionExtension

}
