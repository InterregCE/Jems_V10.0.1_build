package io.cloudflight.ems.api

import io.cloudflight.ems.api.dto.InputProject
import io.cloudflight.ems.api.dto.OutputProject
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping

@Api("Project")
@RequestMapping("/api")
interface ProjectApi {

    @ApiOperation("Returns all project applications")
    @GetMapping("/projects")
    fun getProjects(pageable: Pageable): Page<OutputProject>

    @ApiOperation("Creates new project application")
    @PostMapping("/project")
    fun createProject(@RequestBody project: InputProject): OutputProject

}
