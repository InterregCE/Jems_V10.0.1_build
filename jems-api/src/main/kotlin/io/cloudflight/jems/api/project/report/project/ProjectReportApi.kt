package io.cloudflight.jems.api.project.report.project

import io.cloudflight.jems.api.plugin.dto.PreConditionCheckResultDTO
import io.cloudflight.jems.api.project.dto.report.project.ProjectReportDTO
import io.cloudflight.jems.api.project.dto.report.project.ProjectReportStatusDTO
import io.cloudflight.jems.api.project.dto.report.project.ProjectReportSummaryDTO
import io.cloudflight.jems.api.project.dto.report.project.ProjectReportUpdateDTO
import io.swagger.annotations.Api
import io.swagger.annotations.ApiImplicitParam
import io.swagger.annotations.ApiImplicitParams
import io.swagger.annotations.ApiOperation
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody

@Api("Project Report")
interface ProjectReportApi {

    companion object {
        const val ENDPOINT_API_PROJECT_REPORT_PREFIX = "/api/project/report/byProjectId/{projectId}"
        const val ENDPOINT_API_PROJECT_REPORT = "$ENDPOINT_API_PROJECT_REPORT_PREFIX/byReportId/{reportId}"
    }

    @ApiOperation("Returns all project report summaries by project id")
    @ApiImplicitParams(
        ApiImplicitParam(paramType = "query", name = "page", dataType = "integer"),
        ApiImplicitParam(paramType = "query", name = "size", dataType = "integer"),
        ApiImplicitParam(paramType = "query", name = "sort", dataType = "string")
    )
    @GetMapping(ENDPOINT_API_PROJECT_REPORT_PREFIX)
    fun getProjectReportList(
        @PathVariable projectId: Long,
        pageable: Pageable,
    ): Page<ProjectReportSummaryDTO>

    @ApiOperation("Returns project report detail")
    @GetMapping(ENDPOINT_API_PROJECT_REPORT)
    fun getProjectReport(
        @PathVariable projectId: Long,
        @PathVariable reportId: Long,
    ): ProjectReportDTO

    @ApiOperation("Creates new project report")
    @PostMapping(ENDPOINT_API_PROJECT_REPORT_PREFIX, consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun createProjectReport(
        @PathVariable projectId: Long,
        @RequestBody data: ProjectReportUpdateDTO,
    ): ProjectReportDTO

    @ApiOperation("Update base data of project report")
    @PutMapping(ENDPOINT_API_PROJECT_REPORT, consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateProjectReport(
        @PathVariable projectId: Long,
        @PathVariable reportId: Long,
        @RequestBody data: ProjectReportUpdateDTO,
    ): ProjectReportDTO

    @ApiOperation("Deletes project report")
    @DeleteMapping(ENDPOINT_API_PROJECT_REPORT)
    fun deleteProjectReport(@PathVariable projectId: Long, @PathVariable reportId: Long)

    @ApiOperation("Run pre-submission check for project report")
    @PostMapping("$ENDPOINT_API_PROJECT_REPORT/preCheck/")
    fun runPreCheck(
        @PathVariable projectId: Long,
        @PathVariable reportId: Long,
    ): PreConditionCheckResultDTO

    @ApiOperation("Submit and lock project report")
    @PostMapping("$ENDPOINT_API_PROJECT_REPORT/submit")
    fun submitProjectReport(
        @PathVariable projectId: Long,
        @PathVariable reportId: Long,
    ): ProjectReportStatusDTO
}
