package io.cloudflight.ems.api

import io.cloudflight.ems.api.dto.InputProject
import io.cloudflight.ems.api.dto.OutputProject
import io.swagger.annotations.Api
import io.swagger.annotations.ApiImplicitParam
import io.swagger.annotations.ApiImplicitParams
import io.swagger.annotations.ApiOperation
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@Api("Project")
@RequestMapping("/api/project")
interface ProjectApi {

    @ApiOperation("Returns all project applications")
    @GetMapping
    @ApiImplicitParams(
        ApiImplicitParam(paramType = "query", name = "page", dataType = "integer"),
        ApiImplicitParam(paramType = "query", name = "size", dataType = "integer"),
        ApiImplicitParam(paramType = "query", name = "sort", dataType = "string")
    )
    fun getProjects(pageable: Pageable): Page<OutputProject>

    @ApiOperation("Creates new project application")
    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun createProject(@Valid @RequestBody project: InputProject): OutputProject

    @ApiOperation("Returns a project application by id")
    @GetMapping("/{id}")
    fun getProjectById(@PathVariable id: Long): ResponseEntity<OutputProject>

}
