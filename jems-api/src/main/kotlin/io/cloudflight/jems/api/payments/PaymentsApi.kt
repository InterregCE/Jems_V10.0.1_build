package io.cloudflight.jems.api.payments

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

@Api("Payments api")
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
    @GetMapping(ENDPOINT_API_PAYMENTS)
    fun getPaymentsToProjects(pageable: Pageable): Page<PaymentToProjectDTO>

    @ApiOperation("Retrieve payment detail and partner data by payment id")
    @GetMapping("${ENDPOINT_API_PAYMENTS}/{paymentId}")
    fun getPaymentDetail(
        @PathVariable paymentId: Long
    ): PaymentDetailDTO

    @ApiOperation("Update installments for a partner of a payment id")
    @PutMapping(
        "${ENDPOINT_API_PAYMENTS}/{paymentId}/partnerInstallments/{partnerId}",
        consumes = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun updatePaymentPartnerInstallments(
        @PathVariable paymentId: Long,
        @PathVariable partnerId: Long,
        @RequestBody installments: List<PaymentPartnerInstallmentDTO>
    ): List<PaymentPartnerInstallmentDTO>
}
