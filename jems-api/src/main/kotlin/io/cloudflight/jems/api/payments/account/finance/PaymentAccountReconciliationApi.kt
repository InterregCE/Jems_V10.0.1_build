package io.cloudflight.jems.api.payments.account.finance

import io.cloudflight.jems.api.payments.dto.account.finance.reconciliation.ReconciledAmountPerPriorityDTO
import io.cloudflight.jems.api.payments.dto.account.finance.reconciliation.ReconciledAmountUpdateDTO
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody

@Api("Payment account reconciliation API")
interface PaymentAccountReconciliationApi {
    companion object {
        const val ENDPOINT_API_PAYMENT_ACCOUNT_RECONCILIATION =
            "/api/payments/accounts/{paymentAccountId}/finance/reconciliation"
    }

    @ApiOperation("Get payment account reconciliation overview")
    @GetMapping(ENDPOINT_API_PAYMENT_ACCOUNT_RECONCILIATION)
    fun getReconciliationOverview(@PathVariable paymentAccountId: Long): List<ReconciledAmountPerPriorityDTO>

    @ApiOperation("Update payment account reconciliation comment")
    @PutMapping(ENDPOINT_API_PAYMENT_ACCOUNT_RECONCILIATION, consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateReconciliationComment(
        @PathVariable paymentAccountId: Long,
        @RequestBody reconciliationUpdate: ReconciledAmountUpdateDTO
    )

}
