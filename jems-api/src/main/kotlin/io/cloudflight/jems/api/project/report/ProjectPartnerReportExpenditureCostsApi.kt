package io.cloudflight.jems.api.project.report

import io.cloudflight.jems.api.project.dto.report.partner.expenditure.ProjectPartnerReportExpenditureCostDTO
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody

@Api("Project Partner Report Expenditure costs")
interface ProjectPartnerReportExpenditureCostsApi {

    companion object {
        const val ENDPOINT_API_PARTNER_REPORT_EXPENDITURE_COSTS =
            "${ProjectPartnerReportApi.ENDPOINT_API_PROJECT_PARTNER_REPORT}/expenditure/byPartnerId/{partnerId}/byReportId/{reportId}"
    }

    @ApiOperation("Returns all expenditure costs by partner id and report id")
    @GetMapping(ENDPOINT_API_PARTNER_REPORT_EXPENDITURE_COSTS)
    fun getProjectPartnerReports(
        @PathVariable partnerId: Long,
        @PathVariable reportId: Long,
    ): List<ProjectPartnerReportExpenditureCostDTO>

    @ApiOperation("Update partner report expenditure costs")
    @PutMapping(ENDPOINT_API_PARTNER_REPORT_EXPENDITURE_COSTS, consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updatePartnerReportExpenditures(
        @PathVariable partnerId: Long,
        @PathVariable reportId: Long,
        @RequestBody expenditureCosts: List<ProjectPartnerReportExpenditureCostDTO>
    ): List<ProjectPartnerReportExpenditureCostDTO>
}
