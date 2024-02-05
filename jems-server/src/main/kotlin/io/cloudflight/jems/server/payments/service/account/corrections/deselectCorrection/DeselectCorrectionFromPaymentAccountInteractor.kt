package io.cloudflight.jems.server.payments.service.account.corrections.deselectCorrection

interface DeselectCorrectionFromPaymentAccountInteractor {

    fun deselectCorrection(correctionId: Long)
}
