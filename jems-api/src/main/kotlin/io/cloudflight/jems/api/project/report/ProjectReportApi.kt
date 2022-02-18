package io.cloudflight.jems.api.project.report

import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerSummaryDTO
import io.cloudflight.jems.api.project.dto.report.ProjectPartnerReportDTO
import io.cloudflight.jems.api.project.dto.report.ProjectPartnerReportSummaryDTO
import io.cloudflight.jems.api.project.dto.report.partner.workPlan.ProjectPartnerReportWorkPackageDTO
import io.swagger.annotations.Api
import io.swagger.annotations.ApiImplicitParam
import io.swagger.annotations.ApiImplicitParams
import io.swagger.annotations.ApiOperation
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam

@Api("Project Report")
interface ProjectReportApi {

    companion object {
        private const val ENDPOINT_API_PROJECT_REPORT = "/api/project/report/"
    }

    @ApiOperation("Returns all project partners for reporting")
    @ApiImplicitParams(
        ApiImplicitParam(paramType = "query", name = "sort", dataType = "string", allowMultiple = true)
    )
    @GetMapping("$ENDPOINT_API_PROJECT_REPORT/listPartners/byProjectId/{projectId}")
    fun getProjectPartnersForReporting(
        @PathVariable projectId: Long,
        sort: Sort,
        @RequestParam(required = false) version: String? = null
    ): List<ProjectPartnerSummaryDTO>

}
