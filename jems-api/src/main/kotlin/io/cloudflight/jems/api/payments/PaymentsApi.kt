package io.cloudflight.jems.api.payments

import io.cloudflight.jems.api.payments.dto.PaymentDetailDTO
import io.cloudflight.jems.api.payments.dto.PaymentPartnerDTO
import io.cloudflight.jems.api.payments.dto.PaymentSearchRequestDTO
import io.cloudflight.jems.api.payments.dto.PaymentToProjectDTO
import io.swagger.annotations.Api
import io.swagger.annotations.ApiImplicitParam
import io.swagger.annotations.ApiImplicitParams
import io.swagger.annotations.ApiOperation
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody

@Api("Payments API")
interface PaymentsApi {

    companion object {
        const val ENDPOINT_API_PAYMENTS = "/api/payments"
    }

    @ApiOperation("Retrieve payments to projects")
    @ApiImplicitParams(
        ApiImplicitParam(paramType = "query", name = "page", dataType = "integer"),
        ApiImplicitParam(paramType = "query", name = "size", dataType = "integer"),
        ApiImplicitParam(paramType = "query", name = "sort", dataType = "string")
    )
    @PostMapping(ENDPOINT_API_PAYMENTS, consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun getPaymentsToProjects(
        pageable: Pageable,
        @RequestBody(required = false) searchRequest: PaymentSearchRequestDTO?,
    ): Page<PaymentToProjectDTO>

    @ApiOperation("Retrieve payment detail and partner data by payment id")
    @GetMapping("${ENDPOINT_API_PAYMENTS}/{paymentId}")
    fun getPaymentDetail(
        @PathVariable paymentId: Long
    ): PaymentDetailDTO

    @ApiOperation("Update installments of a payment id")
    @PutMapping(
        "${ENDPOINT_API_PAYMENTS}/{paymentId}/partnerInstallments/",
        consumes = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun updatePaymentInstallments(
        @PathVariable paymentId: Long,
        @RequestBody partnerPayments: List<PaymentPartnerDTO>,
    ): PaymentDetailDTO
}
