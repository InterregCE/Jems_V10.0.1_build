package io.cloudflight.jems.server.controllerInstitution.service

import io.cloudflight.jems.server.controllerInstitution.service.createControllerInstitution.AssignUsersToInstitutionException
import io.cloudflight.jems.server.controllerInstitution.service.model.ControllerInstitutionUser
import io.cloudflight.jems.server.user.service.UserPersistence
import io.cloudflight.jems.server.user.service.UserRolePersistence
import io.cloudflight.jems.server.user.service.model.UserRolePermission
import org.springframework.stereotype.Service

@Service
class ControllerInstitutionValidator(
    private val userRolePersistence: UserRolePersistence,
    private val userPersistence: UserPersistence
) {

    fun validateInstitutionUsers(newUsers: List<ControllerInstitutionUser>) {
        val newInstitutionUsersEmails = newUsers.map { it.userEmail.lowercase() }.toMutableList()
        val foundUserEmails = getUsersThatCanBePersisted(newInstitutionUsersEmails)
        newInstitutionUsersEmails.removeAll(foundUserEmails)
        if (newInstitutionUsersEmails.isNotEmpty())
            throw AssignUsersToInstitutionException()
    }

    private fun getUsersThatCanBePersisted(newInstitutionUsersEmails: List<String>): List<String> {
        val monitorRoleIds = getMonitorRoleIds()
        return userPersistence.findAllByEmails(newInstitutionUsersEmails)
            .filter { persistedUser -> persistedUser.userRole.id in monitorRoleIds }
            .map { it.email.lowercase() }
    }

    private fun getMonitorRoleIds(): Set<Long> =
        userRolePersistence.findRoleIdsHavingAndNotHavingPermissions(
            needsToHaveAtLeastOneFrom = UserRolePermission.getProjectMonitorPermissions(),
            needsNotToHaveAnyOf = emptySet(),
        )
}
