package io.cloudflight.jems.server.payments.controller.account

import io.cloudflight.jems.api.payments.PaymentAccountApi
import io.cloudflight.jems.api.payments.dto.account.PaymentAccountDTO
import io.cloudflight.jems.api.payments.dto.account.PaymentAccountOverviewDTO
import io.cloudflight.jems.api.payments.dto.account.PaymentAccountUpdateDTO
import io.cloudflight.jems.server.payments.service.account.listPaymentAccount.ListPaymentAccountInteractor
import io.cloudflight.jems.server.payments.service.account.getPaymentAccount.GetPaymentAccountInteractor
import io.cloudflight.jems.server.payments.service.account.toDto
import io.cloudflight.jems.server.payments.service.account.toModel
import io.cloudflight.jems.server.payments.service.account.updatePaymentAccount.UpdatePaymentAccountInteractor
import org.springframework.web.bind.annotation.RestController

@RestController
class PaymentAccountController(
    private val listPaymentAccount: ListPaymentAccountInteractor,
    private val getPaymentAccountInteractor: GetPaymentAccountInteractor,
    private val updatePaymentAccountInteractor: UpdatePaymentAccountInteractor
): PaymentAccountApi {

    override fun listPaymentAccount(): List<PaymentAccountOverviewDTO> =
        listPaymentAccount.listPaymentAccount().toDto()

    override fun getPaymentAccount(paymentAccountId: Long): PaymentAccountDTO =
        getPaymentAccountInteractor.getPaymentAccount(paymentAccountId).toDto()

    override fun updatePaymentAccount(paymentAccountId: Long, paymentAccount: PaymentAccountUpdateDTO): PaymentAccountDTO =
        updatePaymentAccountInteractor.updatePaymentAccount(paymentAccountId, paymentAccount.toModel()).toDto()

}
