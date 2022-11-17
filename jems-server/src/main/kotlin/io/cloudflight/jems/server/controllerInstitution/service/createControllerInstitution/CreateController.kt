package io.cloudflight.jems.server.controllerInstitution.service.createControllerInstitution

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.controllerInstitution.service.ControllerInstitutionPersistence
import io.cloudflight.jems.server.controllerInstitution.authorization.CanCreateControllerInstitution
import io.cloudflight.jems.server.controllerInstitution.service.ControllerInstitutionValidator
import io.cloudflight.jems.server.controllerInstitution.service.controllerInstitutionChanged
import io.cloudflight.jems.server.controllerInstitution.service.model.ControllerInstitution
import io.cloudflight.jems.server.controllerInstitution.service.model.UpdateControllerInstitution
import io.cloudflight.jems.server.user.service.UserPersistence
import io.cloudflight.jems.server.user.service.UserRolePersistence
import io.cloudflight.jems.server.user.service.model.UserRolePermission
import io.cloudflight.jems.server.user.service.model.UserSummary
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CreateController(
    private val controllerInstitutionPersistence: ControllerInstitutionPersistence,
    private val controllerInstitutionValidator: ControllerInstitutionValidator,
    private val userPersistence: UserPersistence,
    private val userRolePersistence: UserRolePersistence,
    private val auditPublisher: ApplicationEventPublisher,
): CreateControllerInteractor {


    @CanCreateControllerInstitution
    @Transactional
    @ExceptionWrapper(UpdateControllerInstitutionException::class)
    override fun createController(controllerInstitution: UpdateControllerInstitution): ControllerInstitution {
        val userSummaries = mutableListOf<UserSummary>()
        if (controllerInstitution.institutionUsers.isNotEmpty()) {
            userSummaries.addAll(userPersistence.findAllByEmails(controllerInstitution.institutionUsers.map { it.userEmail }))
            controllerInstitutionValidator.validateInstitutionUsers(controllerInstitution.institutionUsers, getEmailsOfUsersThatCanBePersisted(userSummaries))
        }
        return this.controllerInstitutionPersistence.createControllerInstitution(controllerInstitution).also {
            val updatedUsers = controllerInstitutionPersistence.updateControllerInstitutionUsers(
                it.id,
                usersToUpdate = controllerInstitution.institutionUsers.toSet()
            )
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


    private fun getEmailsOfUsersThatCanBePersisted(userSummaries: List<UserSummary>): List<String> {
        val monitorRoleIds = userRolePersistence.findRoleIdsHavingAndNotHavingPermissions(
            needsToHaveAtLeastOneFrom = UserRolePermission.getProjectMonitorPermissions(),
            needsNotToHaveAnyOf = emptySet(),
        )
        return userSummaries.filter { persistedUserSummary -> persistedUserSummary.userRole.id in monitorRoleIds }
            .map { it.email.lowercase() }
    }

}
