package io.cloudflight.jems.api.payments

import io.swagger.annotations.Api
import io.swagger.annotations.ApiImplicitParam
import io.swagger.annotations.ApiImplicitParams
import io.swagger.annotations.ApiOperation
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.GetMapping

@Api("Payments api")
interface PaymentsApi {

    companion object {
        private const val ENDPOINT_API_PAYMENTS = "/api/payments"
    }

    @ApiOperation("Retrieve payments to projects")
    @ApiImplicitParams(
        ApiImplicitParam(paramType = "query", name = "page", dataType = "integer"),
        ApiImplicitParam(paramType = "query", name = "size", dataType = "integer"),
        ApiImplicitParam(paramType = "query", name = "sort", dataType = "string")
    )
    @GetMapping(ENDPOINT_API_PAYMENTS)
    fun getPaymentsToProjects(pageable: Pageable): Page<PaymentToProjectDTO>

}
