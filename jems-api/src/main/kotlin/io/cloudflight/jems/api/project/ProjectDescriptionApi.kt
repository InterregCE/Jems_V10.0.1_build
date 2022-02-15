package io.cloudflight.jems.api.project

import io.cloudflight.jems.api.project.dto.description.InputProjectLongTermPlans
import io.cloudflight.jems.api.project.dto.description.InputProjectManagement
import io.cloudflight.jems.api.project.dto.description.InputProjectOverallObjective
import io.cloudflight.jems.api.project.dto.description.InputProjectPartnership
import io.cloudflight.jems.api.project.dto.description.InputProjectRelevance
import io.cloudflight.jems.api.project.dto.description.OutputProjectDescription
import io.cloudflight.jems.api.project.dto.description.OutputProjectLongTermPlans
import io.cloudflight.jems.api.project.dto.description.OutputProjectManagement
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import javax.validation.Valid

@Api("Project Description")
interface ProjectDescriptionApi {

    companion object {
        private const val ENDPOINT_API_PROJECT_DESCRIPTION = "/api/project/{projectId}/description"
    }

    @ApiOperation("Retrieve all project description sections")
    @GetMapping(ENDPOINT_API_PROJECT_DESCRIPTION)
    fun getProjectDescription(
        @PathVariable projectId: Long,
        @RequestParam(required = false) version: String? = null
    ): OutputProjectDescription

    @ApiOperation("Update project overall objective")
    @PutMapping("$ENDPOINT_API_PROJECT_DESCRIPTION/c1", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateProjectOverallObjective(
        @PathVariable projectId: Long,
        @RequestBody overallObjective: InputProjectOverallObjective
    ): InputProjectOverallObjective?

    @ApiOperation("Update project relevance")
    @PutMapping("$ENDPOINT_API_PROJECT_DESCRIPTION/c2", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateProjectRelevance(
        @PathVariable projectId: Long,
        @Valid @RequestBody projectRelevance: InputProjectRelevance
    ): InputProjectRelevance

    @ApiOperation("Update project partnership")
    @PutMapping("$ENDPOINT_API_PROJECT_DESCRIPTION/c3", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateProjectPartnership(
        @PathVariable projectId: Long,
        @RequestBody projectPartnership: InputProjectPartnership
    ): InputProjectPartnership?

    @ApiOperation("Update project management data")
    @PutMapping("$ENDPOINT_API_PROJECT_DESCRIPTION/c7", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateProjectManagement(
        @PathVariable projectId: Long,
        @RequestBody projectManagement: InputProjectManagement
    ): OutputProjectManagement

    @ApiOperation("Update project long term plans")
    @PutMapping("$ENDPOINT_API_PROJECT_DESCRIPTION/c8", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateProjectLongTermPlans(
        @PathVariable projectId: Long,
        @RequestBody projectLongTermPlans: InputProjectLongTermPlans
    ): OutputProjectLongTermPlans

}
