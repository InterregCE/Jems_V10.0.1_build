package io.cloudflight.jems.api.payments.account.finance

import io.cloudflight.jems.api.payments.account.PaymentAccountApi
import io.cloudflight.jems.api.payments.dto.account.finance.withdrawn.AmountWithdrawnPerPriorityDTO
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@Api("Payment Account Withdrawn")
interface PaymentAccountWithdrawnApi {

    companion object {
        const val ENDPOINT_API_PAYMENT_ACCOUNT_WITHDRAWN =
            "${PaymentAccountApi.ENDPOINT_API_PAYMENT_ACCOUNT}/finance/withdrawn/{paymentAccountId}"
    }

    @ApiOperation("Get payment account withdrawn overview")
    @GetMapping(ENDPOINT_API_PAYMENT_ACCOUNT_WITHDRAWN)
    fun getWithdrawnOverview(@PathVariable paymentAccountId: Long): List<AmountWithdrawnPerPriorityDTO>

}
