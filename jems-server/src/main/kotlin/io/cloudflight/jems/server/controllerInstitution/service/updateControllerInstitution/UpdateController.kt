package io.cloudflight.jems.server.controllerInstitution.service.updateControllerInstitution

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.controllerInstitution.ControllerInstitutionPersistence
import io.cloudflight.jems.server.controllerInstitution.authorization.CanUpdateControllerInstitution
import io.cloudflight.jems.server.controllerInstitution.service.createControllerInstitution.UpdateControllerInstitutionException
import io.cloudflight.jems.server.controllerInstitution.service.model.ControllerInstitution
import io.cloudflight.jems.server.controllerInstitution.service.model.ControllerInstitutionUser
import io.cloudflight.jems.server.controllerInstitution.service.model.UpdateControllerInstitution
import io.cloudflight.jems.server.project.service.projectuser.assign_user_collaborator_to_project.UsersAreNotValid
import io.cloudflight.jems.server.user.service.UserPersistence
import io.cloudflight.jems.server.user.service.UserRolePersistence
import io.cloudflight.jems.server.user.service.model.UserRolePermission
import io.cloudflight.jems.server.user.service.model.UserSummary
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdateController(
    private val persistence: ControllerInstitutionPersistence,
    private val userRolePersistence: UserRolePersistence,
    private val userPersistence: UserPersistence
): UpdateControllerInteractor {

    @CanUpdateControllerInstitution
    @Transactional
    @ExceptionWrapper(UpdateControllerInstitutionException::class)
    override fun updateControllerInstitution(institutionId: Long, controllerInstitution: UpdateControllerInstitution): ControllerInstitution {
        if (controllerInstitution.institutionUsers.isNotEmpty()) {
            validateAllUsersAreValid(controllerInstitution.institutionUsers)
        }
        return persistence.updateControllerInstitution(controllerInstitution)
    }

    private fun validateAllUsersAreValid(
        institutionUsers: List<ControllerInstitutionUser>
    ) {
        val userEmails = institutionUsers.map { it.userEmail.lowercase() }.toHashSet()
        val foundUserEmails = getUsersThatCanBePersisted(userEmails).mapTo(HashSet()) { it.email.lowercase() }
        userEmails.removeAll(foundUserEmails)
        if (userEmails.isNotEmpty()) throw UsersAreNotValid(emails = userEmails)
    }

    private fun getUsersThatCanBePersisted(userEmails: Set<String>): List<UserSummary> {
        return userPersistence.findAllByEmails(emails = userEmails)
                .filter { persistedUser -> persistedUser.userRole.id in getAvailableRoleIds() }
    }

    private fun getAvailableRoleIds() =
        userRolePersistence.findRoleIdsHavingAndNotHavingPermissions(
            needsToHaveAtLeastOneFrom = UserRolePermission.getProjectMonitorPermissions(),
            needsNotToHaveAnyOf = emptySet(),
        )

}
