package io.cloudflight.jems.api.project.report

import io.cloudflight.jems.api.project.dto.report.partner.financialOverview.ExpenditureCoFinancingBreakdownDTO
import io.cloudflight.jems.api.project.dto.report.partner.financialOverview.ExpenditureCostCategoryBreakdownDTO
import io.cloudflight.jems.api.project.report.ProjectPartnerReportApi.Companion.ENDPOINT_API_PROJECT_PARTNER_REPORT
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@Api("Project Partner Report Financial Overview")
interface ProjectPartnerReportFinancialOverviewApi {

    companion object {
        private const val ENDPOINT_API_PROJECT_PARTNER_REPORT_IDENTIFICATION =
            "$ENDPOINT_API_PROJECT_PARTNER_REPORT/financialOverview/byPartnerId/{partnerId}/byReportId/{reportId}"
    }

    @ApiOperation("Returns Partner Report Expenditure CoFinancing and Funds overview")
    @GetMapping("$ENDPOINT_API_PROJECT_PARTNER_REPORT_IDENTIFICATION/coFinancing")
    fun getCoFinancingBreakdown(
        @PathVariable partnerId: Long,
        @PathVariable reportId: Long,
    ): ExpenditureCoFinancingBreakdownDTO

    @ApiOperation("Returns Partner Report Expenditure breakdown into Cost Categories")
    @GetMapping("$ENDPOINT_API_PROJECT_PARTNER_REPORT_IDENTIFICATION/costCategories")
    fun getCostCategoriesBreakdown(
        @PathVariable partnerId: Long,
        @PathVariable reportId: Long,
    ): ExpenditureCostCategoryBreakdownDTO

}
