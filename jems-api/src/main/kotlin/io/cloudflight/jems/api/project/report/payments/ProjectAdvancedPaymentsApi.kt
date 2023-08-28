package io.cloudflight.jems.api.project.report.payments

import io.cloudflight.jems.api.payments.dto.AdvancePaymentDTO
import io.swagger.annotations.Api
import io.swagger.annotations.ApiImplicitParam
import io.swagger.annotations.ApiImplicitParams
import io.swagger.annotations.ApiOperation
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@Api("Project Advance Payments")
interface ProjectAdvancedPaymentsApi {

    companion object {
        const val ENDPOINT_API_PROJECT_ADVANCED_PAYMENT = "/api/project/report/advancePayment"
    }

    @ApiOperation("Retrieve advance payments for project")
    @ApiImplicitParams(
        ApiImplicitParam(paramType = "query", name = "page", dataType = "integer"),
        ApiImplicitParam(paramType = "query", name = "size", dataType = "integer"),
        ApiImplicitParam(paramType = "query", name = "sort", dataType = "string")
    )
    @GetMapping("$ENDPOINT_API_PROJECT_ADVANCED_PAYMENT/byProjectId/{projectId}")
    fun getAdvancePayments(
        @PathVariable projectId: Long,
        pageable: Pageable
    ): Page<AdvancePaymentDTO>
}