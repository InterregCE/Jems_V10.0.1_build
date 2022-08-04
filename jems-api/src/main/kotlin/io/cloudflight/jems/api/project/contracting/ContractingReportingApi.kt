package io.cloudflight.jems.api.project.contracting

import io.cloudflight.jems.api.project.dto.contracting.reporting.ProjectContractingReportingScheduleDTO
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody

@Api("Project Contracting Reporting")
interface ContractingReportingApi {

    companion object {
        private const val ENDPOINT_API_CONTRACTING_REPORTING = "/api/project/{projectId}/contracting/reporting"
    }

    @ApiOperation("Get Project Contracting Reporting Schedules")
    @GetMapping(ENDPOINT_API_CONTRACTING_REPORTING)
    fun getReportingSchedule(@PathVariable projectId: Long): List<ProjectContractingReportingScheduleDTO>

    @ApiOperation("Update Project Contracting Reporting Schedules")
    @PutMapping(ENDPOINT_API_CONTRACTING_REPORTING, consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateReportingSchedule(
        @PathVariable projectId: Long,
        @RequestBody deadlines: List<ProjectContractingReportingScheduleDTO>,
    ): List<ProjectContractingReportingScheduleDTO>

}
