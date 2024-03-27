package io.cloudflight.jems.server.payments.service.account.finance.correction.getAvailableClosedCorrections

import io.cloudflight.jems.server.payments.model.account.finance.correction.PaymentAccountCorrectionLinking
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface GetAvailableClosedCorrectionsForPaymentAccountInteractor {

    fun getClosedCorrections(pageable: Pageable, paymentAccountId: Long): Page<PaymentAccountCorrectionLinking>
}
