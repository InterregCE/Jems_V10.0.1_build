package io.cloudflight.ems.api.project

import io.cloudflight.ems.api.project.dto.description.InputProjectLongTermPlans
import io.cloudflight.ems.api.project.dto.description.InputProjectManagement
import io.cloudflight.ems.api.project.dto.description.InputProjectOverallObjective
import io.cloudflight.ems.api.project.dto.description.InputProjectPartnership
import io.cloudflight.ems.api.project.dto.description.InputProjectRelevance
import io.cloudflight.ems.api.project.dto.description.OutputProjectDescription
import io.cloudflight.ems.api.project.dto.description.OutputProjectLongTermPlans
import io.cloudflight.ems.api.project.dto.description.OutputProjectManagement
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import javax.validation.Valid
import javax.validation.constraints.Size

@Api("Project Description")
@RequestMapping("/api/project/{id}/description")
interface ProjectDescriptionApi {

    @ApiOperation("Retrieve all project description sections")
    @GetMapping
    fun getProjectDescription(@PathVariable id: Long): OutputProjectDescription

    @ApiOperation("Update project overall objective")
    @PutMapping("/c1", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateProjectOverallObjective(
        @PathVariable id: Long,
        @Valid @RequestBody overallObjective: InputProjectOverallObjective
    ): String?

    @ApiOperation("Update project relevance")
    @PutMapping("/c2", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateProjectRelevance(
        @PathVariable id: Long,
        @Valid @RequestBody project: InputProjectRelevance
    ): InputProjectRelevance

    @ApiOperation("Update project partnership")
    @PutMapping("/c3", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateProjectPartnership(
        @PathVariable id: Long,
        @Valid @RequestBody partnership: InputProjectPartnership
    ): String?

    @ApiOperation("Update project management data")
    @PutMapping("/c7", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateProjectManagement(
        @PathVariable id: Long,
        @Valid @RequestBody project: InputProjectManagement
    ): OutputProjectManagement

    @ApiOperation("Update project long term plans")
    @PutMapping("/c8", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateProjectLongTermPlans(
        @PathVariable id: Long,
        @Valid @RequestBody project: InputProjectLongTermPlans
    ): OutputProjectLongTermPlans

}
