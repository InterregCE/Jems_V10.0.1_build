package io.cloudflight.jems.server.project.controller.projectuser

import io.cloudflight.jems.api.project.ProjectUserCollaboratorApi
import io.cloudflight.jems.api.project.dto.assignment.ProjectCollaboratorLevelDTO
import io.cloudflight.jems.api.project.dto.assignment.ProjectUserCollaboratorDTO
import io.cloudflight.jems.api.project.dto.assignment.UpdateProjectUserCollaboratorDTO
import io.cloudflight.jems.server.project.service.projectuser.assign_user_collaborator_to_project.AssignUserCollaboratorToProjectInteractor
import io.cloudflight.jems.server.project.service.projectuser.get_my_collaborator_level.GetMyCollaboratorLevelInteractor
import io.cloudflight.jems.server.project.service.projectuser.get_user_collaborators_assigned_to_projects.GetUserCollaboratorsAssignedToProjectsInteractor
import org.springframework.web.bind.annotation.RestController

@RestController
class ProjectUserCollaboratorController(
    private val assignUserCollaboratorToProject: AssignUserCollaboratorToProjectInteractor,
    private val getUserCollaboratorsAssignedToProjects: GetUserCollaboratorsAssignedToProjectsInteractor,
    private val getMyCollaboratorLevel: GetMyCollaboratorLevelInteractor,
) : ProjectUserCollaboratorApi {

    override fun listAssignedUserCollaborators(projectId: Long): List<ProjectUserCollaboratorDTO> =
        getUserCollaboratorsAssignedToProjects.getUserIdsForProject(projectId).toDto()

    override fun updateAssignedUserCollaborators(
        projectId: Long,
        users: Set<UpdateProjectUserCollaboratorDTO>
    ): List<ProjectUserCollaboratorDTO> =
        assignUserCollaboratorToProject.updateUserAssignmentsOnProject(projectId, users.toModel()).toDto()

    override fun checkMyProjectLevel(projectId: Long): ProjectCollaboratorLevelDTO? =
        getMyCollaboratorLevel.getMyCollaboratorLevel(projectId).let {
            return if (it != null)
                ProjectCollaboratorLevelDTO.valueOf(it.name)
            else
                null
        }

}
