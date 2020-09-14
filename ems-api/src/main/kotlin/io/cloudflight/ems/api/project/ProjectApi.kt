package io.cloudflight.ems.api.project

import io.cloudflight.ems.api.project.dto.InputProject
import io.cloudflight.ems.api.project.dto.InputProjectData
import io.cloudflight.ems.api.project.dto.InputProjectLongTermPlans
import io.cloudflight.ems.api.project.dto.InputProjectManagement
import io.cloudflight.ems.api.project.dto.OutputProject
import io.cloudflight.ems.api.project.dto.OutputProjectSimple
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

    @ApiOperation("Creates new project application")
    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun createProject(@Valid @RequestBody project: InputProject): OutputProject

    @ApiOperation("Returns a project application by id")
    @GetMapping("/{id}")
    fun getProjectById(@PathVariable id: Long): OutputProject

    @ApiOperation("Update project-related data")
    @PutMapping("/{id}", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateProjectData(@PathVariable id: Long, @Valid @RequestBody project: InputProjectData): OutputProject

    @ApiOperation("Update project-related data")
    @PutMapping("/{id}/management", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateProjectManagement(@PathVariable id: Long, @Valid @RequestBody project: InputProjectManagement): OutputProject

    @ApiOperation("Update project-related data")
    @PutMapping("/{id}/plans", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateProjectLongTermPlans(@PathVariable id: Long, @Valid @RequestBody project: InputProjectLongTermPlans): OutputProject

}
