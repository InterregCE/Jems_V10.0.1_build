package io.cloudflight.jems.api.project.report

import io.cloudflight.jems.api.project.dto.report.partner.PartnerReportExpenditureCostDTO
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody

@Api("Partner Report Expenditure costs")
interface PartnerReportExpenditureCostsApi {

    companion object {
        const val ENDPOINT_API_PARTNER_REPORT_EXPENDITURE_COSTS = "/api/project/report/expenditure/costs"
    }

    @ApiOperation("Returns all expenditure costs by partner id and report id")
    @GetMapping("$ENDPOINT_API_PARTNER_REPORT_EXPENDITURE_COSTS/{partnerId}/{reportId}")
    fun getProjectPartnerReports(
        @PathVariable partnerId: Long,
        @PathVariable reportId: Long,
    ): List<PartnerReportExpenditureCostDTO>

    @ApiOperation("Update partner report expenditure costs")
    @PutMapping(
        "$ENDPOINT_API_PARTNER_REPORT_EXPENDITURE_COSTS/update/{partnerId}/{reportId}",
        consumes = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun updatePartnerReportExpenditures(
        @PathVariable partnerId: Long,
        @PathVariable reportId: Long,
        @RequestBody expenditureCosts: List<PartnerReportExpenditureCostDTO>
    ): List<PartnerReportExpenditureCostDTO>
}
