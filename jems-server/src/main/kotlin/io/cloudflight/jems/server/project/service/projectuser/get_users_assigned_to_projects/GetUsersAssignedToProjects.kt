package io.cloudflight.jems.server.project.service.projectuser.get_users_assigned_to_projects

import io.cloudflight.jems.api.project.dto.assignment.ProjectWithUsersDTO
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.controller.map
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.projectuser.UserProjectPersistence
import io.cloudflight.jems.server.user.service.authorization.CanAssignUsersToProjects
import io.cloudflight.jems.server.user.service.model.assignment.ProjectWithUsers
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetUsersAssignedToProjects(
    private val projectPersistence: ProjectPersistence,
    private val userProjectPersistence: UserProjectPersistence,
) : GetUsersAssignedToProjectsInteractor {

    @CanAssignUsersToProjects
    @Transactional
    @ExceptionWrapper(GetUsersAssignedToProjectsException::class)
    override fun getProjectsWithAssignedUsers(
        pageable: Pageable,
        searchRequest: ProjectWithUsersDTO?
    ): Page<ProjectWithUsers> {
        return projectPersistence.getAssignedProjects(pageable, searchRequest?.map()).map {
            ProjectWithUsers(
                id = it.id.toString(),
                customIdentifier = it.customIdentifier,
                acronym = it.acronym,
                projectStatus = it.status,
                relatedCall = it.callName,
                users = userProjectPersistence.getUsersForProject(it.id),
            )
        }
    }
}
