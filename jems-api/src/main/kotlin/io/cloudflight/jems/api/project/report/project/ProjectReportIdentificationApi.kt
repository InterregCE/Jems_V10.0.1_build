package io.cloudflight.jems.api.project.report.project

import io.cloudflight.jems.api.project.dto.report.project.identification.ProjectReportIdentificationDTO
import io.cloudflight.jems.api.project.dto.report.project.identification.UpdateProjectReportIdentificationDTO
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody

@Api("Project Report Identification")
interface ProjectReportIdentificationApi {

    companion object {
        private const val ENDPOINT_API_PROJECT_REPORT_IDENTIFICATION = "/api/project/report/identification/byProjectId/{projectId}"
    }

    @ApiOperation("Returns project report identification")
    @GetMapping("${ENDPOINT_API_PROJECT_REPORT_IDENTIFICATION}/byReportId/{reportId}")
    fun getProjectReportIdentification(
        @PathVariable projectId: Long,
        @PathVariable reportId: Long,
    ): ProjectReportIdentificationDTO

    @ApiOperation("Updates project report identification")
    @PutMapping("${ENDPOINT_API_PROJECT_REPORT_IDENTIFICATION}/byReportId/{reportId}", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateProjectReportIdentification(
        @PathVariable projectId: Long,
        @PathVariable reportId: Long,
        @RequestBody identification: UpdateProjectReportIdentificationDTO
    ): ProjectReportIdentificationDTO
}
