package io.cloudflight.jems.api.project

import io.cloudflight.jems.api.project.dto.ProjectCallSettingsDTO
import io.cloudflight.jems.api.project.dto.InputProject
import io.cloudflight.jems.api.project.dto.InputProjectData
import io.cloudflight.jems.api.project.dto.OutputProject
import io.cloudflight.jems.api.project.dto.OutputProjectSimple
import io.cloudflight.jems.api.project.dto.budget.ProjectPartnerBudgetDTO
import io.swagger.annotations.Api
import io.swagger.annotations.ApiImplicitParam
import io.swagger.annotations.ApiImplicitParams
import io.swagger.annotations.ApiOperation
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import javax.validation.Valid

@Api("Project")
@RequestMapping("/api/project")
interface ProjectApi {

    @ApiOperation("Returns all project applications")
    @ApiImplicitParams(
        ApiImplicitParam(paramType = "query", name = "page", dataType = "integer"),
        ApiImplicitParam(paramType = "query", name = "size", dataType = "integer"),
        ApiImplicitParam(paramType = "query", name = "sort", dataType = "string")
    )
    @GetMapping
    fun getProjects(pageable: Pageable): Page<OutputProjectSimple>

    @ApiOperation("Returns a project application by id")
    @GetMapping("/{projectId}")
    fun getProjectById(@PathVariable projectId: Long): OutputProject

    @ApiOperation("Returns call setting of a call related to this application")
    @GetMapping("/{projectId}/callSettings")
    fun getProjectCallSettingsById(@PathVariable projectId: Long): ProjectCallSettingsDTO

    @ApiOperation("Creates new project application")
    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun createProject(@Valid @RequestBody project: InputProject): OutputProject

    @ApiOperation("Update project-related data")
    @PutMapping("/{projectId}", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateProjectData(@PathVariable projectId: Long, @Valid @RequestBody project: InputProjectData): OutputProject

    @ApiOperation("Returns project budget for all partners")
    @GetMapping("/{projectId}/budget")
    fun getProjectBudget(@PathVariable projectId: Long): List<ProjectPartnerBudgetDTO>

}
