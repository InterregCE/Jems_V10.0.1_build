package io.cloudflight.jems.api.project.result

import io.cloudflight.jems.api.project.dto.result.IndicatorOverviewLineDTO
import io.cloudflight.jems.api.project.dto.result.ProjectResultUpdateRequestDTO
import io.cloudflight.jems.api.project.dto.result.ProjectResultDTO
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam

@Api("Project Result")
interface ProjectResultApi {

    companion object {
        private const val ENDPOINT_API_PROJECT_RESULT = "/api/project/{projectId}/result"
    }

    @ApiOperation("Returns all project results")
    @GetMapping(ENDPOINT_API_PROJECT_RESULT)
    fun getProjectResults(
        @PathVariable projectId: Long,
        @RequestParam(required = false) version: String? = null
    ): List<ProjectResultDTO>

    @ApiOperation("Creates or updates project results")
    @PutMapping(ENDPOINT_API_PROJECT_RESULT, consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateProjectResults(
        @PathVariable projectId: Long,
        @RequestBody projectResultUpdateRequests: List<ProjectResultUpdateRequestDTO>
    ): List<ProjectResultDTO>

    @ApiOperation("Calculate and return all data needed for project indicators overview table A4")
    @GetMapping("$ENDPOINT_API_PROJECT_RESULT/indicatorsOverview")
    fun getProjectResultIndicatorOverview(
        @PathVariable projectId: Long,
        @RequestParam(required = false) version: String? = null
    ): List<IndicatorOverviewLineDTO>

}
