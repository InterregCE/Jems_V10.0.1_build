package io.cloudflight.jems.api.project

import io.cloudflight.jems.api.project.dto.result.ProjectResultDTO
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import javax.validation.Valid
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping

@Api("Project Result")
@RequestMapping("/api/project/{projectId}/result")
interface ProjectResultApi {
    @ApiOperation("Returns all project results")
    @GetMapping
    fun getProjectResults(@PathVariable projectId: Long): Set<ProjectResultDTO>

    @ApiOperation("Creates or updates project results")
    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateProjectResults(@PathVariable projectId: Long, @Valid @RequestBody projectResults: Set<ProjectResultDTO>): Set<ProjectResultDTO>
}
