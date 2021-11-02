package io.cloudflight.jems.server.project.controller

import io.cloudflight.jems.api.project.ProjectUserApi
import io.cloudflight.jems.api.project.dto.ProjectUserDTO
import io.cloudflight.jems.api.project.dto.UpdateProjectUserDTO
import io.cloudflight.jems.server.user.service.userproject.assign_user_to_project.AssignUserToProjectInteractor
import io.cloudflight.jems.server.user.service.userproject.get_users_assigned_to_projects.GetUsersAssignedToProjectsInteractor
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.RestController

@RestController
class ProjectUserController(
    private val assignUserToProjectInteractor: AssignUserToProjectInteractor,
    private val getUsersAssignedToProjectsInteractor: GetUsersAssignedToProjectsInteractor,
) : ProjectUserApi {

    override fun listProjectsWithAssignedUsers(pageable: Pageable): Page<ProjectUserDTO> =
        getUsersAssignedToProjectsInteractor.getProjectsWithAssignedUsers(pageable).toDto()

    override fun updateProjectUserAssignments(projectUsers: Set<UpdateProjectUserDTO>) =
        projectUsers.forEach {
            assignUserToProjectInteractor.updateUserAssignmentsOnProject(
                projectId = it.projectId,
                userIdsToRemove = it.userIdsToRemove,
                userIdsToAssign = it.userIdsToAdd,
            )
        }

}
