package io.cloudflight.jems.api.payments.applicationToEc

import io.cloudflight.jems.api.payments.dto.PaymentToEcAmountSummaryDTO
import io.cloudflight.jems.api.payments.dto.PaymentToEcLinkingDTO
import io.cloudflight.jems.api.payments.dto.PaymentToEcLinkingUpdateDTO
import io.cloudflight.jems.api.payments.dto.PaymentToEcOverviewTypeDTO
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
import org.springframework.web.bind.annotation.RequestParam

@Api("Payment to EC Linking API")
interface PaymentToEcPaymentLinkingApi {

    companion object {
        private const val ENDPOINT_API_PAYMENT_TO_EC_LINKING = "${PaymentApplicationToEcApi.ENDPOINT_API_EC_PAYMENTS}/paymentLinking"
    }

    @ApiOperation("Returns all FTLS payments to ec whose articles not 94/95")
    @ApiImplicitParams(
        ApiImplicitParam(paramType = "query", name = "page", dataType = "integer"),
        ApiImplicitParam(paramType = "query", name = "size", dataType = "integer"),
        ApiImplicitParam(paramType = "query", name = "sort", dataType = "string")
    )
    @GetMapping("$ENDPOINT_API_PAYMENT_TO_EC_LINKING/{ecApplicationId}/artNot94Not95/ftls")
    fun getFTLSPaymentsLinkedWithEcForArtNot94Not95(pageable: Pageable, @PathVariable ecApplicationId: Long): Page<PaymentToEcLinkingDTO>

    @ApiOperation("Returns all regular payments to ec whose articles not 94/95")
    @ApiImplicitParams(
        ApiImplicitParam(paramType = "query", name = "page", dataType = "integer"),
        ApiImplicitParam(paramType = "query", name = "size", dataType = "integer"),
        ApiImplicitParam(paramType = "query", name = "sort", dataType = "string")
    )
    @GetMapping("$ENDPOINT_API_PAYMENT_TO_EC_LINKING/{ecApplicationId}/artNot94Not95/regular")
    fun getRegularPaymentsLinkedWithEcForArtNot94Not95(pageable: Pageable, @PathVariable ecApplicationId: Long): Page<PaymentToEcLinkingDTO>

    @ApiOperation("Select payment to ec")
    @PutMapping("$ENDPOINT_API_PAYMENT_TO_EC_LINKING/{paymentId}/selectFor/{ecApplicationId}")
    fun selectPaymentToEcPayment(@PathVariable ecApplicationId: Long, @PathVariable paymentId: Long)

    @ApiOperation("Deselect payment from ec")
    @PutMapping("$ENDPOINT_API_PAYMENT_TO_EC_LINKING/{paymentId}/deselect")
    fun deselectPaymentFromEcPayment(@PathVariable paymentId: Long)

    @ApiOperation("Update amounts for linked payment")
    @PutMapping("$ENDPOINT_API_PAYMENT_TO_EC_LINKING/{paymentId}/correctContributions", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateLinkedPayment(
        @PathVariable paymentId: Long,
        @RequestBody paymentToEcLinkingUpdate: PaymentToEcLinkingUpdateDTO,
    )

    @ApiOperation("Get current overview amounts by type")
    @GetMapping("$ENDPOINT_API_PAYMENT_TO_EC_LINKING/{paymentId}/overviewByType")
    fun getPaymentApplicationToEcOverviewAmountsByType(
        @PathVariable paymentId: Long,
        @RequestParam(required = false) type: PaymentToEcOverviewTypeDTO? = null
    ): PaymentToEcAmountSummaryDTO

    @ApiOperation("Get cumulative overview amounts per priority axis")
    @GetMapping("$ENDPOINT_API_PAYMENT_TO_EC_LINKING/{paymentId}/cumulativeOverview")
    fun getPaymentApplicationToEcCumulativeOverview(@PathVariable paymentId: Long,): PaymentToEcAmountSummaryDTO
}
