package io.cloudflight.jems.server.payments.controller.account

import io.cloudflight.jems.api.payments.account.PaymentAccountApi
import io.cloudflight.jems.api.payments.dto.account.PaymentAccountDTO
import io.cloudflight.jems.api.payments.dto.account.PaymentAccountOverviewDTO
import io.cloudflight.jems.api.payments.dto.account.PaymentAccountStatusDTO
import io.cloudflight.jems.api.payments.dto.account.PaymentAccountUpdateDTO
import io.cloudflight.jems.api.payments.dto.account.finance.PaymentAccountAmountSummaryDTO
import io.cloudflight.jems.server.payments.service.account.finalizePaymentAccount.FinalizePaymentAccountInteractor
import io.cloudflight.jems.server.payments.service.account.finance.getAmountSummary.GetPaymentAccountAmountSummaryInteractor
import io.cloudflight.jems.server.payments.service.account.getPaymentAccount.GetPaymentAccountInteractor
import io.cloudflight.jems.server.payments.service.account.listPaymentAccount.ListPaymentAccountInteractor
import io.cloudflight.jems.server.payments.service.account.reOpenPaymentAccount.ReOpenPaymentAccountInteractor
import io.cloudflight.jems.server.payments.service.account.toDto
import io.cloudflight.jems.server.payments.service.account.toModel
import io.cloudflight.jems.server.payments.service.account.updatePaymentAccount.UpdatePaymentAccountInteractor
import io.cloudflight.jems.server.payments.service.toDto
import org.springframework.web.bind.annotation.RestController

@RestController
class PaymentAccountController(
    private val listPaymentAccount: ListPaymentAccountInteractor,
    private val getPaymentAccountInteractor: GetPaymentAccountInteractor,
    private val updatePaymentAccountInteractor: UpdatePaymentAccountInteractor,
    private val finalizePaymentAccountInteractor: FinalizePaymentAccountInteractor,
    private val reOpenPaymentAccountInteractor: ReOpenPaymentAccountInteractor,
    private val getPaymentAccountSummaryInteractor: GetPaymentAccountAmountSummaryInteractor,
) : PaymentAccountApi {

    override fun listPaymentAccount(): List<PaymentAccountOverviewDTO> =
        listPaymentAccount.listPaymentAccount().toDto()

    override fun getPaymentAccount(paymentAccountId: Long): PaymentAccountDTO =
        getPaymentAccountInteractor.getPaymentAccount(paymentAccountId).toDto()

    override fun updatePaymentAccount(
        paymentAccountId: Long,
        paymentAccount: PaymentAccountUpdateDTO
    ): PaymentAccountDTO =
        updatePaymentAccountInteractor.updatePaymentAccount(paymentAccountId, paymentAccount.toModel()).toDto()

    override fun finalizePaymentAccount(paymentAccountId: Long): PaymentAccountStatusDTO =
        finalizePaymentAccountInteractor.finalizePaymentAccount(paymentAccountId).toDto()

    override fun reOpenPaymentAccount(paymentAccountId: Long): PaymentAccountStatusDTO =
        reOpenPaymentAccountInteractor.reOpenPaymentAccount(paymentAccountId).toDto()

    override fun getPaymentAccountAmountSummary(paymentAccountId: Long): PaymentAccountAmountSummaryDTO =
        getPaymentAccountSummaryInteractor.getSummaryOverview(paymentAccountId).toDto()

}
