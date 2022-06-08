package io.cloudflight.jems.api.project

import io.cloudflight.jems.api.project.dto.assignment.ProjectCollaboratorLevelDTO
import io.cloudflight.jems.api.project.dto.assignment.ProjectUserCollaboratorDTO
import io.cloudflight.jems.api.project.dto.assignment.UpdateProjectUserCollaboratorDTO
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody

@Api("Project User Collaborator")
interface ProjectUserCollaboratorApi {

    companion object {
        private const val ENDPOINT_API_PROJECT_USER_COLLABORATOR = "/api/projectUserCollaborator/{projectId}"
    }

    @ApiOperation("Retrieves users that can collaborate on this project within lead applicant group")
    @GetMapping(ENDPOINT_API_PROJECT_USER_COLLABORATOR)
    fun listAssignedUserCollaborators(@PathVariable projectId: Long): List<ProjectUserCollaboratorDTO>

    @ApiOperation("Assigns a list of Users for monitoring to Projects")
    @PutMapping(ENDPOINT_API_PROJECT_USER_COLLABORATOR, consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateAssignedUserCollaborators(
        @PathVariable projectId: Long,
        @RequestBody users: Set<UpdateProjectUserCollaboratorDTO>
    ): List<ProjectUserCollaboratorDTO>

    @ApiOperation("Check my collaborator-related project permissions")
    @GetMapping("$ENDPOINT_API_PROJECT_USER_COLLABORATOR/checkMyLevel")
    fun checkMyProjectLevel(@PathVariable projectId: Long): ProjectCollaboratorLevelDTO?

}
