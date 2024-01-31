package io.cloudflight.jems.api.payments

import io.cloudflight.jems.api.payments.dto.account.PaymentAccountDTO
import io.cloudflight.jems.api.payments.dto.account.PaymentAccountOverviewDTO
import io.cloudflight.jems.api.payments.dto.account.PaymentAccountStatusDTO
import io.cloudflight.jems.api.payments.dto.account.PaymentAccountUpdateDTO
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

@Api("Payment Account")
interface PaymentAccountApi {

    companion object {
        const val ENDPOINT_API_PAYMENT_ACCOUNT = "/api/payments/accounts"
    }

    @ApiOperation("List payment accounts")
    @GetMapping(ENDPOINT_API_PAYMENT_ACCOUNT)
    fun listPaymentAccount(): List<PaymentAccountOverviewDTO>

    @ApiOperation("Update payment account")
    @PutMapping("$ENDPOINT_API_PAYMENT_ACCOUNT/{paymentAccountId}", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updatePaymentAccount(
        @PathVariable paymentAccountId: Long,
        @RequestBody paymentAccount: PaymentAccountUpdateDTO
    ): PaymentAccountDTO

    @ApiOperation("Get payment account")
    @GetMapping("$ENDPOINT_API_PAYMENT_ACCOUNT/{paymentAccountId}")
    fun getPaymentAccount(
        @PathVariable paymentAccountId: Long,
    ): PaymentAccountDTO

    @ApiOperation("Finish payment account")
    @PostMapping("$ENDPOINT_API_PAYMENT_ACCOUNT/{paymentAccountId}/finalize")
    fun finalizePaymentAccount(
        @PathVariable paymentAccountId: Long
    ): PaymentAccountStatusDTO

    @ApiOperation("Re-open payment account")
    @PostMapping("$ENDPOINT_API_PAYMENT_ACCOUNT/{paymentAccountId}/reOpen")
    fun reOpenPaymentAccount(
        @PathVariable paymentAccountId: Long
    ): PaymentAccountStatusDTO

}
