package io.cloudflight.jems.api.project.overview

import io.cloudflight.jems.api.project.dto.report.overview.resultIndicator.ProjectReportResultIndicatorLivingTableDTO
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@Api("Project Overview Result Living Table Api")
interface ProjectOverviewResultLivingTableApi {

    companion object {
        const val ENDPOINT_API_PROJECT_REPORTING_RESULT_OVERVIEW = "/api/project/{projectId}/overview/result"
    }

    @ApiOperation("Retrieve indicator living table for project")
    @GetMapping(ENDPOINT_API_PROJECT_REPORTING_RESULT_OVERVIEW)
    fun getResultOverview(
        @PathVariable projectId: Long
    ): List<ProjectReportResultIndicatorLivingTableDTO>
}

