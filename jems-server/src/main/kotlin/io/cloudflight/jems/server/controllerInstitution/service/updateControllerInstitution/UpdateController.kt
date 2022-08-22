package io.cloudflight.jems.server.controllerInstitution.service.updateControllerInstitution

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.controllerInstitution.ControllerInstitutionPersistence
import io.cloudflight.jems.server.controllerInstitution.authorization.CanUpdateControllerInstitution
import io.cloudflight.jems.server.controllerInstitution.service.ControllerInstitutionValidator
import io.cloudflight.jems.server.controllerInstitution.service.controllerInstitutionChanged
import io.cloudflight.jems.server.controllerInstitution.service.createControllerInstitution.UpdateControllerInstitutionException
import io.cloudflight.jems.server.controllerInstitution.service.model.ControllerInstitution
import io.cloudflight.jems.server.controllerInstitution.service.model.ControllerInstitutionUser
import io.cloudflight.jems.server.controllerInstitution.service.model.UpdateControllerInstitution
import io.cloudflight.jems.server.project.service.projectuser.UserProjectPersistence
import io.cloudflight.jems.server.user.service.UserPersistence
import io.cloudflight.jems.server.user.service.UserRolePersistence
import io.cloudflight.jems.server.user.service.model.UserRolePermission
import io.cloudflight.jems.server.user.service.model.UserSummary
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdateController(
    private val persistence: ControllerInstitutionPersistence,
    private val userPersistence: UserPersistence,
    private val userRolePersistence: UserRolePersistence,
    private val userProjectPersistence: UserProjectPersistence,
    private val auditPublisher: ApplicationEventPublisher,
    private val controllerInstitutionValidator: ControllerInstitutionValidator
): UpdateControllerInteractor {

    @CanUpdateControllerInstitution
    @Transactional
    @ExceptionWrapper(UpdateControllerInstitutionException::class)
    override fun updateControllerInstitution(
        institutionId: Long,
        controllerInstitution: UpdateControllerInstitution
    ): ControllerInstitution {

        val userSummaries = mutableListOf<UserSummary>()
        controllerInstitution.institutionUsers.takeIf { it.isNotEmpty() }?.let { institutionUsers ->
            userSummaries.addAll(userPersistence.findAllByEmails(institutionUsers.map { it.userEmail }))
            controllerInstitutionValidator.validateInstitutionUsers(
                controllerInstitution.institutionUsers,
                getEmailsOfUsersThatCanBePersisted(userSummaries)
            )
        }
        val institutionUsersToUpdate = controllerInstitution.institutionUsers
        val existingInstitutionUsers = persistence.getInstitutionUsersByInstitutionId(institutionId)
        val userIdsToDelete = institutionUsersToUpdate.getUserIdsToDelete(existingInstitutionUsers)
        val userIdsToAdd = institutionUsersToUpdate.getUserIdsToAdd(existingInstitutionUsers, userSummaries)

        val updatedUsers = persistence.updateControllerInstitutionUsers(
            institutionId,
            usersToUpdate = institutionUsersToUpdate.toSet(),
            usersIdsToDelete = userIdsToDelete
        )

        updateInstitutionUsersProjectAssignment(
            institutionId = institutionId,
            userIdsToAdd = userIdsToAdd,
            userIdsToRemove = userIdsToDelete,
        )

        return persistence.updateControllerInstitution(controllerInstitution).also {
            it.institutionUsers.addAll(updatedUsers)
            auditPublisher.publishEvent(
                controllerInstitutionChanged(
                    context = this,
                    controllerInstitution = it,
                    nutsRegion3 = controllerInstitution.institutionNuts
                )
            )
        }
    }

    private fun updateInstitutionUsersProjectAssignment(
        institutionId: Long,
        userIdsToAdd: Set<Long>,
        userIdsToRemove: Set<Long>
    ) {
        if (userIdsToAdd.isNotEmpty() || userIdsToRemove.isNotEmpty()) {
            val assignmentsPartnerProjectIds = persistence.getInstitutionPartnerAssignmentsByInstitutionId(institutionId)
                .takeIf { it.isNotEmpty() }?.let { institutionAssignments ->
                    institutionAssignments.map { it.partnerProjectId }
                }

            val partnerProjectIdToAssignedInstitutionUsers =
                persistence.getInstitutionPartnerAssignmentsWithUsersByPartnerProjectIdsIn(assignmentsPartnerProjectIds?.toSet() ?: emptySet())
                    .filter { it.institutionId != institutionId }
                    .groupBy(keySelector = { it.partnerProjectId }, valueTransform = { it.userId })

            assignmentsPartnerProjectIds?.forEach { projectId ->
                userProjectPersistence.changeUsersAssignedToProject(
                    projectId,
                    userIdsToAssign = userIdsToAdd.minus((partnerProjectIdToAssignedInstitutionUsers[projectId] ?: emptySet()).toSet()),
                    userIdsToRemove = userIdsToRemove.minus((partnerProjectIdToAssignedInstitutionUsers[projectId] ?: emptySet()).toSet())
                )
            }
        }
    }
    private fun List<ControllerInstitutionUser>.getUserIdsToDelete(existingInstitutionUsers: List<ControllerInstitutionUser>) =
        existingInstitutionUsers.map { it.userId }.toSet().minus(this.map { it.userId }.toSet())

    private fun List<ControllerInstitutionUser>.getUserIdsToAdd(
        existingInstitutionUsers: List<ControllerInstitutionUser>,
        userSummaries: List<UserSummary>
    ): Set<Long>  {
        val newInstitutionUserEmails = this.map { it.userEmail }.minus(existingInstitutionUsers.map { it.userEmail }.toSet())
        return userSummaries.filter { it.email in newInstitutionUserEmails }.map { it.id }.toSet()
    }

    private fun getEmailsOfUsersThatCanBePersisted(userSummaries: List<UserSummary>): List<String> {
        val monitorRoleIds = userRolePersistence.findRoleIdsHavingAndNotHavingPermissions(
            needsToHaveAtLeastOneFrom = UserRolePermission.getProjectMonitorPermissions(),
            needsNotToHaveAnyOf = emptySet(),
        )
        return userSummaries.filter { persistedUserSummary -> persistedUserSummary.userRole.id in monitorRoleIds }
            .map { it.email.lowercase() }
    }
}
