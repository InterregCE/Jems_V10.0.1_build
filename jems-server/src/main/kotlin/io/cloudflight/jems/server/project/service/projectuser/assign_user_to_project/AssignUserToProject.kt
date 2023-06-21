package io.cloudflight.jems.server.project.service.projectuser.assign_user_to_project

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.projectuser.UserProjectPersistence
import io.cloudflight.jems.server.user.service.UserPersistence
import io.cloudflight.jems.server.user.service.UserRolePersistence
import io.cloudflight.jems.server.user.service.authorization.CanAssignUsersToProjects
import io.cloudflight.jems.server.user.service.model.UserRolePermission.Companion.getGlobalProjectRetrievePermissions
import io.cloudflight.jems.server.user.service.model.UserRolePermission.Companion.getProjectMonitorPermissions
import io.cloudflight.jems.server.user.service.model.assignment.UpdateProjectUser
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AssignUserToProject(
    private val userPersistence: UserPersistence,
    private val userRolePersistence: UserRolePersistence,
    private val projectPersistence: ProjectPersistence,
    private val userProjectPersistence: UserProjectPersistence,
    private val eventPublisher: ApplicationEventPublisher
) : AssignUserToProjectInteractor {

    @CanAssignUsersToProjects
    @Transactional
    @ExceptionWrapper(AssignUserToProjectException::class)
    override fun updateUserAssignmentsOnProject(data: Set<UpdateProjectUser>) {
        val availableUserIds = getAvailableUserIdsByIdsAndRoles(
            userIds = data.map { it.userIdsToAdd }.flatten().toSet(),
            userRoleIds = getAvailableUserRoleIds(),
        )

        data.filter { (availableUserIds intersect it.userIdsToAdd).isNotEmpty() || it.userIdsToRemove.isNotEmpty() }
            .forEach {
                val userIds = userProjectPersistence.changeUsersAssignedToProject(
                    projectId = it.projectId,
                    userIdsToRemove = it.userIdsToRemove,
                    userIdsToAssign = availableUserIds intersect it.userIdsToAdd,
                )
                eventPublisher.publishEvent(
                    AssignUserToProjectEvent(
                        project = projectPersistence.getProjectSummary(it.projectId),
                        users = userPersistence.findAllByIds(userIds),
                    )
                )
            }
    }

    private fun getAvailableUserRoleIds() = userRolePersistence.findRoleIdsHavingAndNotHavingPermissions(
        needsToHaveAtLeastOneFrom = getGlobalProjectRetrievePermissions() union getProjectMonitorPermissions(),
        needsNotToHaveAnyOf = emptySet(),
    )

    private fun getAvailableUserIdsByIdsAndRoles(userIds: Set<Long>, userRoleIds: Set<Long>) =
        userPersistence.findAllByIds(userIds)
            .filter { userRoleIds.contains(it.userRole.id) }
            .map { it.id }
}
