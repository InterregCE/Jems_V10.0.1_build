package io.cloudflight.jems.api.payments.applicationToEc

import io.cloudflight.jems.api.payments.dto.PaymentToEcCorrectionExtensionDTO
import io.cloudflight.jems.api.payments.dto.PaymentToEcCorrectionLinkingDTO
import io.cloudflight.jems.api.payments.dto.PaymentToEcCorrectionLinkingUpdateDTO
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

@Api("Correction linking API")
interface CorrectionToEcPaymentLinkingApi {

    companion object {
        private const val ENDPOINT_API_PAYMENT_TO_EC_CORRECTION_LINKING = "${PaymentApplicationToEcApi.ENDPOINT_API_EC_PAYMENTS}/correctionLinking"
    }

    @ApiOperation("Returns all closed corrections that can be included")
    @ApiImplicitParams(
        ApiImplicitParam(paramType = "query", name = "page", dataType = "integer"),
        ApiImplicitParam(paramType = "query", name = "size", dataType = "integer"),
        ApiImplicitParam(paramType = "query", name = "sort", dataType = "string")
    )
    @GetMapping("${ENDPOINT_API_PAYMENT_TO_EC_CORRECTION_LINKING}/{ecApplicationId}/corrections")
    fun getAvailableCorrections(pageable: Pageable, @PathVariable ecApplicationId: Long): Page<PaymentToEcCorrectionLinkingDTO>

    @ApiOperation("Select correction to ec")
    @PutMapping("${ENDPOINT_API_PAYMENT_TO_EC_CORRECTION_LINKING}/{correctionId}/selectFor/{ecApplicationId}")
    fun selectCorrectionToEcPayment(@PathVariable ecApplicationId: Long, @PathVariable correctionId: Long)

    @ApiOperation("Deselect correction from ec")
    @PutMapping("${ENDPOINT_API_PAYMENT_TO_EC_CORRECTION_LINKING}/{correctionId}/deselect")
    fun deselectCorrectionFromEcPayment(@PathVariable correctionId: Long)

    @ApiOperation("Update amounts for linked correction")
    @PutMapping("${ENDPOINT_API_PAYMENT_TO_EC_CORRECTION_LINKING}/{correctionId}/correctContributions", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateLinkedCorrection(
        @PathVariable correctionId: Long,
        @RequestBody  correctionLinkingUpdate: PaymentToEcCorrectionLinkingUpdateDTO,
    ): PaymentToEcCorrectionExtensionDTO
}
