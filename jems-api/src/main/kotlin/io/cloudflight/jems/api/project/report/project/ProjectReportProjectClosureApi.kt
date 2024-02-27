package io.cloudflight.jems.api.project.report.project

import io.cloudflight.jems.api.project.dto.report.project.projectClosure.ProjectReportProjectClosureDTO
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody

@Api("Project Report Project Closure")
interface ProjectReportProjectClosureApi {

    companion object {
        private const val ENDPOINT_API_PROJECT_REPORT_PROJECT_CLOSURE = "${ProjectReportApi.ENDPOINT_API_PROJECT_REPORT}/projectClosure"
    }

    @ApiOperation("Gets project report project closure")
    @GetMapping(ENDPOINT_API_PROJECT_REPORT_PROJECT_CLOSURE)
    fun getProjectClosure(
        @PathVariable projectId: Long,
        @PathVariable reportId: Long
    ): ProjectReportProjectClosureDTO

    @ApiOperation("Updates project report project closure")
    @PutMapping(ENDPOINT_API_PROJECT_REPORT_PROJECT_CLOSURE, consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateProjectClosure(
        @PathVariable projectId: Long,
        @PathVariable reportId: Long,
        @RequestBody projectClosure: ProjectReportProjectClosureDTO
    ): ProjectReportProjectClosureDTO
}
