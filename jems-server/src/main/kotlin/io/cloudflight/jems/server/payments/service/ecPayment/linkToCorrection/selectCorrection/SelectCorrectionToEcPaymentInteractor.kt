package io.cloudflight.jems.server.payments.service.ecPayment.linkToCorrection.selectCorrection

interface SelectCorrectionToEcPaymentInteractor {

    fun selectCorrectionToEcPayment(correctionId: Long, ecPaymentId: Long)

}
