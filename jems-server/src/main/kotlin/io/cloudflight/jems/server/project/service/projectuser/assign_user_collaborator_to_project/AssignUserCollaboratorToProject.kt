package io.cloudflight.jems.server.project.service.projectuser.assign_user_collaborator_to_project

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.entity.projectuser.CollaboratorLevel
import io.cloudflight.jems.server.project.service.projectuser.UserProjectCollaboratorPersistence
import io.cloudflight.jems.server.user.service.UserPersistence
import io.cloudflight.jems.server.user.service.UserRolePersistence
import io.cloudflight.jems.server.user.service.authorization.CanUpdateCollaborators
import io.cloudflight.jems.server.user.service.model.UserRolePermission.ProjectCreate
import io.cloudflight.jems.server.user.service.model.UserSummary
import io.cloudflight.jems.server.user.service.model.assignment.CollaboratorAssignedToProject
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AssignUserCollaboratorToProject(
    private val userPersistence: UserPersistence,
    private val userRolePersistence: UserRolePersistence,
    private val collaboratorPersistence: UserProjectCollaboratorPersistence,
) : AssignUserCollaboratorToProjectInteractor {

    @CanUpdateCollaborators
    @Transactional
    @ExceptionWrapper(AssignUserCollaboratorToProjectException::class)
    override fun updateUserAssignmentsOnProject(
        projectId: Long,
        emailsWithLevel: Set<Pair<String, CollaboratorLevel>>
    ): List<CollaboratorAssignedToProject> {
        val allowedRoleIds = getAvailableRoleIds()
        val emailToLevelMap = emailsWithLevel.associateBy({ it.first }, { it.second })
        val usersToBePersistedThatCanBePersisted = userPersistence.findAllByEmails(emails = emailToLevelMap.keys)
            .filter { allowedRoleIds.contains(it.userRole.id) }.associateWith { emailToLevelMap[it.email]!! }

        validateAllUsersAreValid(
            requestedUsers = emailsWithLevel,
            availUsers = usersToBePersistedThatCanBePersisted,
        )
        validateAtLeastOneManagerInGroup(
            levels = usersToBePersistedThatCanBePersisted.values,
        )

        return collaboratorPersistence.changeUsersAssignedToProject(
            projectId = projectId,
            usersToPersist = usersToBePersistedThatCanBePersisted.mapKeys { it.key.id }
        )
    }

    private fun getAvailableRoleIds() =
        userRolePersistence.findRoleIdsHavingAndNotHavingPermissions(
            needsToHaveAtLeastOneFrom = setOf(ProjectCreate),
            needsNotToHaveAnyOf = emptySet(),
        )

    private fun validateAllUsersAreValid(requestedUsers: Set<Pair<String, CollaboratorLevel>>, availUsers: Map<UserSummary, CollaboratorLevel>) {
        val foundUserEmails = availUsers.mapTo(HashSet()) { it.key.email }
        val notFoundUserEmails = requestedUsers.mapTo(HashSet()) { it.first }
        notFoundUserEmails.removeAll(foundUserEmails)

        if (notFoundUserEmails.isNotEmpty())
            throw UsersAreNotValid(emails = notFoundUserEmails)
    }

    private fun validateAtLeastOneManagerInGroup(levels: Collection<CollaboratorLevel>) {
        if (levels.all { it != CollaboratorLevel.MANAGE })
            throw MinOneManagingCollaboratorRequiredException()
    }

}
