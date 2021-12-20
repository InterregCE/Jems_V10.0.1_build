package io.cloudflight.jems.server.project.service.projectuser.get_users_assigned_to_projects

import io.cloudflight.jems.server.user.service.model.assignment.ProjectWithUsers
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface GetUsersAssignedToProjectsInteractor {
    fun getProjectsWithAssignedUsers(pageable: Pageable): Page<ProjectWithUsers>
}
