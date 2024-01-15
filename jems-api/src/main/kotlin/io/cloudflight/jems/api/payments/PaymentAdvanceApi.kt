package io.cloudflight.jems.api.payments

import io.cloudflight.jems.api.payments.dto.AdvancePaymentDTO
import io.cloudflight.jems.api.payments.dto.AdvancePaymentDetailDTO
import io.cloudflight.jems.api.payments.dto.AdvancePaymentSearchRequestDTO
import io.cloudflight.jems.api.payments.dto.AdvancePaymentStatusUpdateDTO
import io.cloudflight.jems.api.payments.dto.AdvancePaymentUpdateDTO
import io.swagger.annotations.Api
import io.swagger.annotations.ApiImplicitParam
import io.swagger.annotations.ApiImplicitParams
import io.swagger.annotations.ApiOperation
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody

@Api("Advance Payments")
interface PaymentAdvanceApi {

    companion object {
        const val ENDPOINT_API_ADV_PAYMENTS = "/api/advancePayment"
    }

    @ApiOperation("Retrieve advance payments")
    @ApiImplicitParams(
        ApiImplicitParam(paramType = "query", name = "page", dataType = "integer"),
        ApiImplicitParam(paramType = "query", name = "size", dataType = "integer"),
        ApiImplicitParam(paramType = "query", name = "sort", dataType = "string")
    )
    @PostMapping(ENDPOINT_API_ADV_PAYMENTS, consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun getAdvancePayments(
        pageable: Pageable,
        @RequestBody(required = false) searchRequest: AdvancePaymentSearchRequestDTO?,
    ): Page<AdvancePaymentDTO>

    @ApiOperation("Retrieve advance payment detail by advance payment id")
    @GetMapping("${ENDPOINT_API_ADV_PAYMENTS}/{paymentId}")
    fun getAdvancePaymentDetail(
        @PathVariable paymentId: Long
    ): AdvancePaymentDetailDTO

    @ApiOperation("Update/Create advance payment")
    @PutMapping(ENDPOINT_API_ADV_PAYMENTS, consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateAdvancePayment(
        @RequestBody advancePayment: AdvancePaymentUpdateDTO
    ): AdvancePaymentDetailDTO

    @ApiOperation("Update advance payment status")
    @PutMapping("$ENDPOINT_API_ADV_PAYMENTS/{paymentId}/status", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateAdvancePaymentStatus(
        @PathVariable paymentId: Long,
        @RequestBody status: AdvancePaymentStatusUpdateDTO
    )

    @ApiOperation("Delete advance payment by id")
    @DeleteMapping("${ENDPOINT_API_ADV_PAYMENTS}/{paymentId}")
    fun deleteAdvancePayment(@PathVariable paymentId: Long)
}
