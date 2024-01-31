package io.cloudflight.jems.api.payments.applicationToEc

import io.cloudflight.jems.api.payments.dto.PaymentToEcAmountSummaryDTO
import io.cloudflight.jems.api.payments.dto.PaymentToEcLinkingDTO
import io.cloudflight.jems.api.payments.dto.PaymentToEcLinkingUpdateDTO
import io.cloudflight.jems.api.payments.dto.PaymentToEcOverviewTypeDTO
import io.cloudflight.jems.api.payments.dto.PaymentTypeDTO
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
        private const val ENDPOINT_API_PAYMENT_TO_EC_LINKING = "${PaymentApplicationToEcApi.ENDPOINT_API_EC_PAYMENTS}/{ecPaymentId}/paymentLinking"
    }

    @ApiOperation("Returns all payments whose articles is 94/95")
    @ApiImplicitParams(
        ApiImplicitParam(paramType = "query", name = "page", dataType = "integer"),
        ApiImplicitParam(paramType = "query", name = "size", dataType = "integer"),
        ApiImplicitParam(paramType = "query", name = "sort", dataType = "string")
    )
    @GetMapping("$ENDPOINT_API_PAYMENT_TO_EC_LINKING/{projectPaymentType}/art94Art95")
    fun getPaymentsLinkedWithEcForArt94OrArt95(
        pageable: Pageable,
        @PathVariable ecPaymentId: Long,
        @PathVariable projectPaymentType: PaymentTypeDTO
    ): Page<PaymentToEcLinkingDTO>

    @ApiOperation("Returns all payments whose articles is NOT 94/95")
    @ApiImplicitParams(
        ApiImplicitParam(paramType = "query", name = "page", dataType = "integer"),
        ApiImplicitParam(paramType = "query", name = "size", dataType = "integer"),
        ApiImplicitParam(paramType = "query", name = "sort", dataType = "string")
    )
    @GetMapping("$ENDPOINT_API_PAYMENT_TO_EC_LINKING/{projectPaymentType}/notArt94NotArt95")
    fun getPaymentsLinkedWithEcNotArt94NotArt95(
        pageable: Pageable,
        @PathVariable ecPaymentId: Long,
        @PathVariable projectPaymentType: PaymentTypeDTO
    ): Page<PaymentToEcLinkingDTO>


    @ApiOperation("Select payment to ec")
    @PutMapping("$ENDPOINT_API_PAYMENT_TO_EC_LINKING/select/{paymentId}")
    fun selectPaymentToEcPayment(@PathVariable ecPaymentId: Long, @PathVariable paymentId: Long)

    @ApiOperation("Deselect payment from ec")
    @PutMapping("$ENDPOINT_API_PAYMENT_TO_EC_LINKING/deselect/{paymentId}")
    fun deselectPaymentFromEcPayment(@PathVariable ecPaymentId: Long, @PathVariable paymentId: Long)

    @ApiOperation("Update amounts for linked payment")
    @PutMapping("$ENDPOINT_API_PAYMENT_TO_EC_LINKING/{paymentId}/correctContributions", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateLinkedPayment(
        @PathVariable ecPaymentId: Long,
        @PathVariable paymentId: Long,
        @RequestBody paymentToEcLinkingUpdate: PaymentToEcLinkingUpdateDTO,
    )

    @ApiOperation("Get current overview amounts by type")
    @GetMapping("$ENDPOINT_API_PAYMENT_TO_EC_LINKING/overviewByType")
    fun getPaymentApplicationToEcOverviewAmountsByType(
        @PathVariable ecPaymentId: Long,
        @RequestParam(required = false) type: PaymentToEcOverviewTypeDTO? = null
    ): PaymentToEcAmountSummaryDTO

    @ApiOperation("Get cumulative overview amounts per priority axis")
    @GetMapping("$ENDPOINT_API_PAYMENT_TO_EC_LINKING/cumulativeOverview")
    fun getPaymentApplicationToEcCumulativeOverview(@PathVariable ecPaymentId: Long,): PaymentToEcAmountSummaryDTO
}
