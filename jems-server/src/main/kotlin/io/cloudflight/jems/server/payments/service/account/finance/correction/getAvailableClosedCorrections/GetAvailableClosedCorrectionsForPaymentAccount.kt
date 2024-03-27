package io.cloudflight.jems.server.payments.service.account.finance.correction.getAvailableClosedCorrections

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.payments.authorization.CanRetrievePaymentsAccount
import io.cloudflight.jems.server.payments.model.account.finance.correction.PaymentAccountCorrectionLinking
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetAvailableClosedCorrectionsForPaymentAccount(
    private val paymentAccountCorrectionsService: PaymentAccountCorrectionsService
) : GetAvailableClosedCorrectionsForPaymentAccountInteractor {

    @CanRetrievePaymentsAccount
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetAvailableClosedCorrectionsForPaymentAccountException::class)
    override fun getClosedCorrections(pageable: Pageable, paymentAccountId: Long): Page<PaymentAccountCorrectionLinking> =
        paymentAccountCorrectionsService.getClosedCorrections(pageable, paymentAccountId)


}
