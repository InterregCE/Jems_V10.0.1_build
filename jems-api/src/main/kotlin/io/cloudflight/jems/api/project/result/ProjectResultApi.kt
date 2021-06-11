package io.cloudflight.jems.api.project.result

import io.cloudflight.jems.api.project.dto.result.InputProjectResultDTO
import io.cloudflight.jems.api.project.dto.result.ProjectResultDTO
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Api("Project Result")
@RequestMapping("/api/project/{projectId}/result")
interface ProjectResultApi {

    @ApiOperation("Returns all project results")
    @GetMapping
    fun getProjectResults(@PathVariable projectId: Long, @RequestParam(required = false) version: String? = null): List<ProjectResultDTO>

    @ApiOperation("Creates or updates project results")
    @PutMapping(consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateProjectResults(@PathVariable projectId: Long, @RequestBody projectResults: List<InputProjectResultDTO>): List<ProjectResultDTO>

}
