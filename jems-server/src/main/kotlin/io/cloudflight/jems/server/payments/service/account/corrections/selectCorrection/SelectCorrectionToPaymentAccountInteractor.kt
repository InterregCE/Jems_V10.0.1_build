package io.cloudflight.jems.server.payments.service.account.corrections.selectCorrection

interface SelectCorrectionToPaymentAccountInteractor {

    fun selectCorrection(correctionId: Long, paymentAccountId: Long)
}
