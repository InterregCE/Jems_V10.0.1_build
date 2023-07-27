package io.cloudflight.jems.api.project.report.project.verification

import io.cloudflight.jems.api.project.dto.report.project.verification.expenditure.ProjectReportVerificationExpenditureLineDTO
import io.cloudflight.jems.api.project.dto.report.project.verification.expenditure.ProjectReportVerificationExpenditureLineUpdateDTO
import io.cloudflight.jems.api.project.dto.report.project.verification.expenditure.ProjectReportVerificationRiskBasedDTO
import io.cloudflight.jems.api.project.report.project.verification.ProjectReportVerificationApi.Companion.ENDPOINT_API_PROJECT_REPORT_VERIFICATION
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody

@Api("Project Report Verification Expenditure Verification")
interface ProjectReportVerificationExpenditureAPI {

    companion object {
        private const val ENDPOINT_API_PROJECT_REPORT_VERIFICATION_EXPENDITURE =
            "${ENDPOINT_API_PROJECT_REPORT_VERIFICATION}/expenditure"
    }

    @ApiOperation("Returns all expenditure verification by partner id and report id")
    @GetMapping(ENDPOINT_API_PROJECT_REPORT_VERIFICATION_EXPENDITURE)
    fun getProjectReportExpenditureVerification(
        @PathVariable projectId: Long,
        @PathVariable reportId: Long,
    ): List<ProjectReportVerificationExpenditureLineDTO>

    @ApiOperation("Update project verification report expenditure verification")
    @PutMapping(ENDPOINT_API_PROJECT_REPORT_VERIFICATION_EXPENDITURE, consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateProjectReportExpendituresVerification(
        @PathVariable projectId: Long,
        @PathVariable reportId: Long,
        @RequestBody expenditureVerificationList: List<ProjectReportVerificationExpenditureLineUpdateDTO>
    ): List<ProjectReportVerificationExpenditureLineDTO>

    @ApiOperation("Returns project report verification risk based data")
    @GetMapping("$ENDPOINT_API_PROJECT_REPORT_VERIFICATION_EXPENDITURE/riskBased")
    fun getProjectReportExpenditureVerificationRiskBased(
        @PathVariable projectId: Long,
        @PathVariable reportId: Long,
    ): ProjectReportVerificationRiskBasedDTO

    @ApiOperation("Update project report verification risk based data")
    @PutMapping("$ENDPOINT_API_PROJECT_REPORT_VERIFICATION_EXPENDITURE/riskBased", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateProjectReportExpenditureVerificationRiskBased(
        @PathVariable projectId: Long,
        @PathVariable reportId: Long,
        @RequestBody riskBasedData: ProjectReportVerificationRiskBasedDTO
    ): ProjectReportVerificationRiskBasedDTO

}
