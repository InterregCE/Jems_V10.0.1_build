package io.cloudflight.jems.server.payments.service.account.finance.correction.updateCorrection

import io.cloudflight.jems.server.payments.model.account.PaymentAccountCorrectionExtension
import io.cloudflight.jems.server.payments.model.account.PaymentAccountCorrectionLinkingUpdate

interface UpdateLinkedCorrectionToPaymentAccountInteractor {

    fun updateCorrection(correctionId: Long, correctionLinkingUpdate: PaymentAccountCorrectionLinkingUpdate): PaymentAccountCorrectionExtension
}
