package io.cloudflight.jems.server.payments.service.account.finance.correction.selectCorrection

interface SelectCorrectionToPaymentAccountInteractor {

    fun selectCorrection(correctionId: Long, paymentAccountId: Long)
}
