package io.cloudflight.jems.server.project.service.projectuser.assign_user_to_project

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.projectuser.UserProjectPersistence
import io.cloudflight.jems.server.user.service.UserPersistence
import io.cloudflight.jems.server.user.service.UserRolePersistence
import io.cloudflight.jems.server.user.service.authorization.CanAssignUsersToProjects
import io.cloudflight.jems.server.user.service.model.UserRolePermission
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
        val automaticallyAssignedUsers = getAutomaticallyAssignedUsers()
        val availableUsers = getAvailableUsersByIdsAndRoles(
            userIds = data.map { it.userIdsToAdd }.flatten().toSet(),
            userRoleIds = getAvailableRoleIds(),
        )

        data.filter { (availableUsers.keys intersect it.userIdsToAdd).isNotEmpty() || it.userIdsToRemove.isNotEmpty() }
            .forEach {
                val userIds = userProjectPersistence.changeUsersAssignedToProject(
                    projectId = it.projectId,
                    userIdsToRemove = it.userIdsToRemove,
                    userIdsToAssign = availableUsers.keys intersect it.userIdsToAdd,
                )
                eventPublisher.publishEvent(
                    AssignUserEvent(
                        project = projectPersistence.getProjectSummary(it.projectId),
                        users = automaticallyAssignedUsers.plus(userPersistence.findAllByIds(userIds)),
                    )
                )
            }
    }

    private fun getAutomaticallyAssignedUsers() =
        userPersistence.findAllWithRoleIdIn(
            roleIds = userRolePersistence.findRoleIdsHavingAndNotHavingPermissions(
                needsToHaveAtLeastOneFrom = UserRolePermission.getGlobalProjectRetrievePermissions(),
                needsNotToHaveAnyOf = emptySet(),
            )
        )

    private fun getAvailableRoleIds() =
        userRolePersistence.findRoleIdsHavingAndNotHavingPermissions(
            needsToHaveAtLeastOneFrom = UserRolePermission.getProjectMonitorPermissions(),
            needsNotToHaveAnyOf = UserRolePermission.getGlobalProjectRetrievePermissions(),
        )

    private fun getAvailableUsersByIdsAndRoles(userIds: Set<Long>, userRoleIds: Set<Long>) =
        userPersistence.findAllByIds(userIds)
            .filter { userRoleIds.contains(it.userRole.id) }
            .associateBy { it.id }
}
