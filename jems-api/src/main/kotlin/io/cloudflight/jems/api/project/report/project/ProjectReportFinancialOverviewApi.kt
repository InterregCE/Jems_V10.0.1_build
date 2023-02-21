package io.cloudflight.jems.api.project.report.project

import io.cloudflight.jems.api.project.dto.report.project.financialOverview.CertificateCoFinancingBreakdownDTO
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@Api("Project Report Financial Overview")
interface ProjectReportFinancialOverviewApi {
    companion object {
        private const val ENDPOINT_API_PROJECT_REPORT_IDENTIFICATION =
            "${ProjectReportApi.ENDPOINT_API_PROJECT_REPORT_PREFIX}/financialOverview/byReportId/{reportId}"
    }

    @ApiOperation("Returns Project Report Expenditure CoFinancing and Funds overview")
    @GetMapping("${ENDPOINT_API_PROJECT_REPORT_IDENTIFICATION}/coFinancing")
    fun getCoFinancingBreakdown(
        @PathVariable projectId: Long,
        @PathVariable reportId: Long,
    ): CertificateCoFinancingBreakdownDTO

    @ApiOperation("Returns Project Report Expenditure breakdown into Cost Categories")
    @GetMapping("${ENDPOINT_API_PROJECT_REPORT_IDENTIFICATION}/costCategories")
    fun getCostCategoriesBreakdown(
        @PathVariable projectId: Long,
        @PathVariable reportId: Long,
    ): CertificateCoFinancingBreakdownDTO
}
