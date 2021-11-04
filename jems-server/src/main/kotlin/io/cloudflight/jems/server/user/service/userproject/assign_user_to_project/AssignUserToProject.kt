package io.cloudflight.jems.server.user.service.userproject.assign_user_to_project

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.user.service.UserPersistence
import io.cloudflight.jems.server.user.service.UserProjectPersistence
import io.cloudflight.jems.server.user.service.authorization.CanAssignUsersToProjects
import io.cloudflight.jems.server.user.service.model.UpdateProjectUser
import io.cloudflight.jems.server.user.service.model.UserRolePermission.ProjectRetrieve
import io.cloudflight.jems.server.user.service.model.UserRolePermission.ProjectRetrieveEditUserAssignments
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AssignUserToProject(
    private val userPersistence: UserPersistence,
    private val projectPersistence: ProjectPersistence,
    private val userProjectPersistence: UserProjectPersistence,
    private val eventPublisher: ApplicationEventPublisher
) : AssignUserToProjectInteractor {

    companion object {
        private val DEFAULT_PERMISSIONS = setOf(
            ProjectRetrieve,
            ProjectRetrieveEditUserAssignments,
        )
    }

    @CanAssignUsersToProjects
    @Transactional
    @ExceptionWrapper(AssignUserToProjectException::class)
    override fun updateUserAssignmentsOnProject(data: Set<UpdateProjectUser>) =
        data
            .filter { it.userIdsToAdd.isNotEmpty() || it.userIdsToRemove.isNotEmpty() }
            .forEach {
                val userIds = userProjectPersistence.changeUsersAssignedToProject(
                    projectId = it.projectId,
                    userIdsToRemove = it.userIdsToRemove,
                    userIdsToAssign = it.userIdsToAdd.filterUsersIdsToThoseWithoutHiddenPermission(),
                )
                eventPublisher.publishEvent(AssignUserEvent(
                    project = projectPersistence.getProjectSummary(it.projectId),
                    users = userPersistence.findAllByIds(userIds)
                ))
            }

    private fun Set<Long>.filterUsersIdsToThoseWithoutHiddenPermission(): Set<Long> =
        this.filterTo(HashSet()) { (userPersistence.getById(it).userRole.permissions intersect DEFAULT_PERMISSIONS).isEmpty() }

}
