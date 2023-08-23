package io.cloudflight.jems.api.project.report.project.verification

import io.cloudflight.jems.api.project.dto.report.project.financialOverview.verification.FinancingSourceBreakdownDTO
import io.cloudflight.jems.api.project.dto.report.project.financialOverview.verification.VerificationWorkOverviewDTO
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@Api("Project Report Verification Overview")
interface ProjectReportVerificationOverviewApi {

    companion object {
        private const val ENDPOINT_API_PROJECT_REPORT_VERIFICATION_OVERVIEW =
            "${ProjectReportVerificationApi.ENDPOINT_API_PROJECT_REPORT_VERIFICATION}/overview"
    }

    @ApiOperation("Returns Project Report Expenditure Verification breakdown into funds and contributions")
    @GetMapping("${ENDPOINT_API_PROJECT_REPORT_VERIFICATION_OVERVIEW}/deductionByTypologyOfErrors")
    fun getDeductionBreakdown(
        @PathVariable projectId: Long,
        @PathVariable reportId: Long,
    ): VerificationWorkOverviewDTO

    @ApiOperation("Returns Project Report Expenditure Verification breakdown into funds and contributions")
    @GetMapping("${ENDPOINT_API_PROJECT_REPORT_VERIFICATION_OVERVIEW}/financingSourceSplit")
    fun getFinancingSourceBreakdown(
        @PathVariable projectId: Long,
        @PathVariable reportId: Long,
    ): FinancingSourceBreakdownDTO

}
