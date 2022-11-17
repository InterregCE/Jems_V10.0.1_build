package io.cloudflight.jems.server.project.controller.projectuser

import io.cloudflight.jems.api.project.ProjectUserApi
import io.cloudflight.jems.api.project.dto.assignment.ProjectUserDTO
import io.cloudflight.jems.api.project.dto.assignment.ProjectWithUsersDTO
import io.cloudflight.jems.api.project.dto.assignment.UpdateProjectUserDTO
import io.cloudflight.jems.server.project.controller.toDto
import io.cloudflight.jems.server.project.controller.toModel
import io.cloudflight.jems.server.project.service.projectuser.assign_user_to_project.AssignUserToProjectInteractor
import io.cloudflight.jems.server.project.service.projectuser.get_users_assigned_to_projects.GetUsersAssignedToProjectsInteractor
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.RestController

@RestController
class ProjectUserController(
    private val assignUserToProjectInteractor: AssignUserToProjectInteractor,
    private val getUsersAssignedToProjectsInteractor: GetUsersAssignedToProjectsInteractor,
) : ProjectUserApi {

    override fun listProjectsWithAssignedUsers(pageable: Pageable, searchRequest: ProjectWithUsersDTO?): Page<ProjectUserDTO> =
        getUsersAssignedToProjectsInteractor.getProjectsWithAssignedUsers(pageable, searchRequest).toDto()

    override fun updateProjectUserAssignments(projectUsers: Set<UpdateProjectUserDTO>) =
        assignUserToProjectInteractor.updateUserAssignmentsOnProject(projectUsers.toModel())

}