package io.cloudflight.jems.api.project.report.project

import io.cloudflight.jems.api.project.dto.report.project.spfContribution.ProjectReportSpfContributionClaimDTO
import io.cloudflight.jems.api.project.dto.report.project.spfContribution.ProjectReportSpfContributionClaimUpdateDTO
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody


@Api("Project Report SPF contribution")
interface ProjectReportSpfContributionApi {

    companion object {
        private const val ENDPOINT_API_PROJECT_REPORT_SPF_CONTRIBUTION =
            "${ProjectReportApi.ENDPOINT_API_PROJECT_REPORT}/spf/contributionClaims"
    }

    @ApiOperation("Returns SPF founding source claims")
    @GetMapping(ENDPOINT_API_PROJECT_REPORT_SPF_CONTRIBUTION)
    fun getContributionClaims(
        @PathVariable projectId: Long,
        @PathVariable reportId: Long,
    ):List<ProjectReportSpfContributionClaimDTO>


    @ApiOperation("Returns SPF founding source claims")
    @PutMapping(ENDPOINT_API_PROJECT_REPORT_SPF_CONTRIBUTION, consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateContributionClaims(
        @PathVariable projectId: Long,
        @PathVariable reportId: Long,
        @RequestBody toUpdate: List<ProjectReportSpfContributionClaimUpdateDTO>
    ): List<ProjectReportSpfContributionClaimDTO>
}
