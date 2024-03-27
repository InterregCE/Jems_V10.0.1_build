package io.cloudflight.jems.server.project.service.partner.assign_user_collaborator_to_partner

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.partner.service.partneruser.assign_user_collaborator_to_partner.AssignUserCollaboratorToPartnerException
import io.cloudflight.jems.server.partner.service.partneruser.assign_user_collaborator_to_partner.UsersAreNotValid
import io.cloudflight.jems.server.project.entity.partneruser.PartnerCollaboratorLevel
import io.cloudflight.jems.server.project.repository.partner.ProjectPartnerRepository
import io.cloudflight.jems.server.project.repository.partner.toModel
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.partner.UserPartnerCollaboratorPersistence
import io.cloudflight.jems.server.user.service.UserPersistence
import io.cloudflight.jems.server.user.service.UserRolePersistence
import io.cloudflight.jems.server.user.service.authorization.CanUpdateCollaborators
import io.cloudflight.jems.server.user.service.model.UserRolePermission.ProjectCreate
import io.cloudflight.jems.server.user.service.model.assignment.PartnerCollaborator
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AssignUserCollaboratorToPartner(
    private val userPersistence: UserPersistence,
    private val userRolePersistence: UserRolePersistence,
    private val partnerCollaboratorPersistence: UserPartnerCollaboratorPersistence,
    private val projectPersistence: ProjectPersistence,
    private val partnerRepository: ProjectPartnerRepository,
    private val eventPublisher: ApplicationEventPublisher,
) : AssignUserCollaboratorToPartnerInteractor {

    @CanUpdateCollaborators
    @Transactional
    @ExceptionWrapper(AssignUserCollaboratorToPartnerException::class)
    override fun updateUserAssignmentsOnPartner(
        projectId: Long,
        partnerId: Long,
        emailsWithLevel: Map<String, Pair<PartnerCollaboratorLevel, Boolean>>
    ): Set<PartnerCollaborator> {
        val emailsToLevelNormalized = emailsWithLevel.mapKeys { it.key.lowercase() }
        val allowedRoleIds = getAvailableRoleIds()
        val usersToBePersisted = userPersistence
            .findAllByEmails(emails = emailsToLevelNormalized.keys)
            .filter { it.userRole.id in allowedRoleIds }
            .map { it.copy(email = it.email.lowercase()) }
            .associateWith { emailsToLevelNormalized[it.email]!! }

        validateAllUsersAreValid(
            requestedUsers = emailsToLevelNormalized.keys,
            availableUsers = usersToBePersisted.keys.mapTo(HashSet()) { it.email },
        )

        val result = partnerCollaboratorPersistence.changeUsersAssignedToPartner(
            projectId = projectId,
            partnerId = partnerId,
            usersToPersist = usersToBePersisted.mapKeys { it.key.id }
        )
        eventPublisher.publishEvent(collaboratorsChangedEvent(projectId, partnerId, result))
        return result
    }

    private fun getAvailableRoleIds() =
        userRolePersistence.findRoleIdsHavingAndNotHavingPermissions(
            needsToHaveAtLeastOneFrom = setOf(ProjectCreate),
            needsNotToHaveAnyOf = emptySet(),
        )

    private fun validateAllUsersAreValid(
        requestedUsers: Set<String>,
        availableUsers: Set<String>
    ) {
        val notFoundUserEmails = requestedUsers.minus(availableUsers)
        if (notFoundUserEmails.isNotEmpty())
            throw UsersAreNotValid(emails = notFoundUserEmails)
    }

    private fun collaboratorsChangedEvent(projectId: Long, partnerId: Long, collaborators: Set<PartnerCollaborator>) =
        AssignUserCollaboratorToPartnerEvent(
            project = projectPersistence.getProjectSummary(projectId),
            partner = partnerRepository.getReferenceById(partnerId).toModel(),
            collaborators = collaborators,
        )

}
