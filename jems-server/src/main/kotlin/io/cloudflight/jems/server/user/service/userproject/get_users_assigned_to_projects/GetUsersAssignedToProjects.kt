package io.cloudflight.jems.server.user.service.userproject.get_users_assigned_to_projects

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.controller.toDTO
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.user.service.UserProjectPersistence
import io.cloudflight.jems.server.user.service.authorization.CanAssignUsersToProjects
import io.cloudflight.jems.server.user.service.model.ProjectWithUsers
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
    override fun getProjectsWithAssignedUsers(pageable: Pageable): Page<ProjectWithUsers> =
        projectPersistence.getProjects(pageable).map { ProjectWithUsers(
            id = it.id,
            customIdentifier = it.customIdentifier,
            acronym = it.acronym,
            projectStatus = it.status,
            assignedUserIds = userProjectPersistence.getUserIdsForProject(it.id),
        ) }

}
