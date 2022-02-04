package io.cloudflight.jems.server.project.service.projectuser.assign_user_collaborator_to_project

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.entity.projectuser.ProjectCollaboratorLevel
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.projectuser.UserProjectCollaboratorPersistence
import io.cloudflight.jems.server.user.service.UserPersistence
import io.cloudflight.jems.server.user.service.UserRolePersistence
import io.cloudflight.jems.server.user.service.authorization.CanUpdateCollaborators
import io.cloudflight.jems.server.user.service.model.UserRolePermission.ProjectCreate
import io.cloudflight.jems.server.user.service.model.UserSummary
import io.cloudflight.jems.server.user.service.model.assignment.CollaboratorAssignedToProject
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AssignUserCollaboratorToProject(
    private val userPersistence: UserPersistence,
    private val userRolePersistence: UserRolePersistence,
    private val collaboratorPersistence: UserProjectCollaboratorPersistence,
    private val projectPersistence: ProjectPersistence,
    private val eventPublisher: ApplicationEventPublisher,
) : AssignUserCollaboratorToProjectInteractor {

    @CanUpdateCollaborators
    @Transactional
    @ExceptionWrapper(AssignUserCollaboratorToProjectException::class)
    override fun updateUserAssignmentsOnProject(
        projectId: Long,
        emailsWithLevel: Set<Pair<String, ProjectCollaboratorLevel>>
    ): List<CollaboratorAssignedToProject> {
        val allowedRoleIds = getAvailableRoleIds()
        val emailToLevelMap = emailsWithLevel.associateBy({ it.first.lowercase() }, { it.second })
        val usersToBePersistedThatCanBePersisted = userPersistence.findAllByEmails(emails = emailToLevelMap.keys)
            .filter { allowedRoleIds.contains(it.userRole.id) }.associateWith { emailToLevelMap[it.email.lowercase()]!! }

        validateAllUsersAreValid(
            requestedUsers = emailsWithLevel,
            availUsers = usersToBePersistedThatCanBePersisted,
        )
        validateAtLeastOneManagerInGroup(
            levels = usersToBePersistedThatCanBePersisted.values,
        )

        val result = collaboratorPersistence.changeUsersAssignedToProject(
            projectId = projectId,
            usersToPersist = usersToBePersistedThatCanBePersisted.mapKeys { it.key.id }
        )
        eventPublisher.publishEvent(collaboratorsChangedEvent(projectId, result))
        return result
    }

    private fun getAvailableRoleIds() =
        userRolePersistence.findRoleIdsHavingAndNotHavingPermissions(
            needsToHaveAtLeastOneFrom = setOf(ProjectCreate),
            needsNotToHaveAnyOf = emptySet(),
        )

    private fun validateAllUsersAreValid(requestedUsers: Set<Pair<String, ProjectCollaboratorLevel>>, availUsers: Map<UserSummary, ProjectCollaboratorLevel>) {
        val foundUserEmails = availUsers.mapTo(HashSet()) { it.key.email.lowercase() }
        val notFoundUserEmails = requestedUsers.mapTo(HashSet()) { it.first.lowercase() }
        notFoundUserEmails.removeAll(foundUserEmails)

        if (notFoundUserEmails.isNotEmpty())
            throw UsersAreNotValid(emails = notFoundUserEmails)
    }

    private fun validateAtLeastOneManagerInGroup(levels: Collection<ProjectCollaboratorLevel>) {
        if (levels.all { it != ProjectCollaboratorLevel.MANAGE })
            throw MinOneManagingCollaboratorRequiredException()
    }

    private fun collaboratorsChangedEvent(id: Long, collaborators: List<CollaboratorAssignedToProject>) = AssignUserCollaboratorEvent(
        project = projectPersistence.getProjectSummary(id),
        collaborators = collaborators,
    )

}
