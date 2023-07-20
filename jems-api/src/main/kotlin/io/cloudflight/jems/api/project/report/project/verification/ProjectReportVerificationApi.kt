package io.cloudflight.jems.api.project.report.project.verification

import io.cloudflight.jems.api.project.dto.report.project.verification.ProjectReportVerificationClarificationDTO
import io.cloudflight.jems.api.project.dto.report.project.verification.ProjectReportVerificationConclusionDTO
import io.cloudflight.jems.api.project.report.project.ProjectReportApi.Companion.ENDPOINT_API_PROJECT_REPORT
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody

@Api("Project Report Verification")
interface ProjectReportVerificationApi {
    companion object{
        const val ENDPOINT_API_PROJECT_REPORT_VERIFICATION = "${ENDPOINT_API_PROJECT_REPORT}/verification"
    }


    @ApiOperation("Returns Report Verification Start Date and Conclusions")
    @GetMapping(ENDPOINT_API_PROJECT_REPORT_VERIFICATION)
    fun getReportVerificationConclusion(
        @PathVariable projectId: Long,
        @PathVariable reportId: Long
    ): ProjectReportVerificationConclusionDTO

    @ApiOperation("Update Report Verification Start Date and Conclusions")
    @PutMapping(ENDPOINT_API_PROJECT_REPORT_VERIFICATION, consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateReportVerificationConclusion(
        @PathVariable projectId: Long,
        @PathVariable reportId: Long,
        @RequestBody conclusion: ProjectReportVerificationConclusionDTO
    ): ProjectReportVerificationConclusionDTO

    @ApiOperation("Returns verification clarification requests")
    @GetMapping("$ENDPOINT_API_PROJECT_REPORT_VERIFICATION/clarifications")
    fun getReportVerificationClarificationRequests(
        @PathVariable projectId: Long,
        @PathVariable reportId: Long,
    ): List<ProjectReportVerificationClarificationDTO>

    @ApiOperation("Creates or Updates report verification clarification requests")
    @PutMapping("$ENDPOINT_API_PROJECT_REPORT_VERIFICATION/clarifications", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateReportVerificationClarifications(
        @PathVariable projectId: Long,
        @PathVariable reportId: Long,
        @RequestBody clarifications: List<ProjectReportVerificationClarificationDTO>
    ): List<ProjectReportVerificationClarificationDTO>
}