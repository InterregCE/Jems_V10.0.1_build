package io.cloudflight.jems.api.payments.account

import io.cloudflight.jems.api.payments.dto.account.PaymentAccountAmountSummaryDTO
import io.cloudflight.jems.api.payments.dto.account.PaymentAccountCorrectionExtensionDTO
import io.cloudflight.jems.api.payments.dto.account.PaymentAccountCorrectionLinkingDTO
import io.cloudflight.jems.api.payments.dto.account.PaymentAccountCorrectionLinkingUpdateDTO
import io.swagger.annotations.Api
import io.swagger.annotations.ApiImplicitParam
import io.swagger.annotations.ApiImplicitParams
import io.swagger.annotations.ApiOperation
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody

@Api("Payment Account Correction Linking API")
interface PaymentAccountCorrectionLinkingApi {

    companion object {
        private const val ENDPOINT_API_PAYMENT_ACCOUNT_CORRECTION_LINKING = "${PaymentAccountApi.ENDPOINT_API_PAYMENT_ACCOUNT}/correctionLinking"
    }

    @ApiOperation("Returns all closed corrections that can be included")
    @ApiImplicitParams(
        ApiImplicitParam(paramType = "query", name = "page", dataType = "integer"),
        ApiImplicitParam(paramType = "query", name = "size", dataType = "integer"),
        ApiImplicitParam(paramType = "query", name = "sort", dataType = "string")
    )
    @GetMapping("${ENDPOINT_API_PAYMENT_ACCOUNT_CORRECTION_LINKING}/{paymentAccountId}/corrections")
    fun getAvailableCorrections(pageable: Pageable, @PathVariable paymentAccountId: Long): Page<PaymentAccountCorrectionLinkingDTO>

    @ApiOperation("Select correction to ec")
    @PutMapping("${ENDPOINT_API_PAYMENT_ACCOUNT_CORRECTION_LINKING}/{correctionId}/selectFor/{paymentAccountId}")
    fun selectCorrectionToPaymentAccount(@PathVariable paymentAccountId: Long, @PathVariable correctionId: Long)

    @ApiOperation("Deselect correction from ec")
    @PutMapping("${ENDPOINT_API_PAYMENT_ACCOUNT_CORRECTION_LINKING}/{correctionId}/deselect")
    fun deselectCorrectionFromPaymentAccount(@PathVariable correctionId: Long)

    @ApiOperation("Update amounts for linked correction")
    @PutMapping("${ENDPOINT_API_PAYMENT_ACCOUNT_CORRECTION_LINKING}/{correctionId}/correctContributions", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateLinkedCorrection(
        @PathVariable correctionId: Long,
        @RequestBody correctionLinkingUpdate: PaymentAccountCorrectionLinkingUpdateDTO,
    ): PaymentAccountCorrectionExtensionDTO

    @ApiOperation("Get current overview amounts per priority axis")
    @GetMapping("${ENDPOINT_API_PAYMENT_ACCOUNT_CORRECTION_LINKING}/{paymentAccountId}/currentOverview")
    fun getCurrentOverview(@PathVariable paymentAccountId: Long): PaymentAccountAmountSummaryDTO
}
