package io.cloudflight.jems.server.payments.controller.account.finance

import io.cloudflight.jems.api.payments.account.finance.PaymentAccountWithdrawnApi
import io.cloudflight.jems.api.payments.dto.account.finance.withdrawn.AmountWithdrawnPerPriorityDTO
import io.cloudflight.jems.server.payments.service.account.finance.withdrawn.getWithdrawnOverview.GetWithdrawnOverviewInteractor
import org.springframework.web.bind.annotation.RestController

@RestController
class PaymentAccountWithdrawnController(
    private val getWithdrawnOverview: GetWithdrawnOverviewInteractor,
) : PaymentAccountWithdrawnApi {

    override fun getWithdrawnOverview(paymentAccountId: Long): List<AmountWithdrawnPerPriorityDTO> =
        getWithdrawnOverview.getWithdrawnOverview(paymentAccountId).toDto()

}
