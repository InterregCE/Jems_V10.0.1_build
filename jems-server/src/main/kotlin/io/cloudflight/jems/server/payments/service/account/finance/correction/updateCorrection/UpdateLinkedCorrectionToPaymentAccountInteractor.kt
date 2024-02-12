package io.cloudflight.jems.server.payments.service.account.finance.correction.updateCorrection

import io.cloudflight.jems.server.payments.model.account.finance.correction.PaymentAccountCorrectionExtension
import io.cloudflight.jems.server.payments.model.account.finance.correction.PaymentAccountCorrectionLinkingUpdate

interface UpdateLinkedCorrectionToPaymentAccountInteractor {

    fun updateCorrection(correctionId: Long, correctionLinkingUpdate: PaymentAccountCorrectionLinkingUpdate): PaymentAccountCorrectionExtension
}
