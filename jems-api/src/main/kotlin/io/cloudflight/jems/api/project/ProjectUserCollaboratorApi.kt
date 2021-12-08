package io.cloudflight.jems.api.project

import io.cloudflight.jems.api.project.dto.assignment.ProjectUserCollaboratorDTO
import io.cloudflight.jems.api.project.dto.assignment.UpdateProjectUserCollaboratorDTO
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping

@Api("Project User Collaborator")
@RequestMapping("/api/projectUserCollaborator/{projectId}")
interface ProjectUserCollaboratorApi {

    @ApiOperation("Retrieves users that can collaborate on this project within lead applicant group")
    @GetMapping
    fun listAssignedUserCollaborators(@PathVariable projectId: Long): List<ProjectUserCollaboratorDTO>

    @ApiOperation("Assigns a list of Users for monitoring to Projects")
    @PutMapping(consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateAssignedUserCollaborators(@PathVariable projectId: Long, @RequestBody users: Set<UpdateProjectUserCollaboratorDTO>): List<ProjectUserCollaboratorDTO>

}
