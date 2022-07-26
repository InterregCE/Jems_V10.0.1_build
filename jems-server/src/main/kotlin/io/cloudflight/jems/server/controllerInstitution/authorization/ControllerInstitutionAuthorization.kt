package io.cloudflight.jems.server.controllerInstitution.authorization

import io.cloudflight.jems.server.authentication.authorization.Authorization
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.controllerInstitution.ControllerInstitutionPersistence
import io.cloudflight.jems.server.user.service.model.UserRolePermission
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Component

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@controllerInstitutionAuthorization.canRetrieveControllerInstitutions()")
annotation class CanRetrieveControllerInstitutions

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@controllerInstitutionAuthorization.canViewControllerInstitutionDetails(#institutionId)")
annotation class CanViewControllerInstitutionDetails

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@controllerInstitutionAuthorization.canCreateControllerInstitution()")
annotation class CanCreateControllerInstitution

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@controllerInstitutionAuthorization.canUpdateControllerInstitution(#institutionId)")
annotation class CanUpdateControllerInstitution

@Component
class ControllerInstitutionAuthorization(
    override val securityService: SecurityService,
    private val controllerInstitutionPersistence: ControllerInstitutionPersistence,
) : Authorization(securityService) {

    fun canRetrieveControllerInstitutions(): Boolean {
        return hasPermission(UserRolePermission.InstitutionsRetrieve) ||
            hasPermission(UserRolePermission.InstitutionsUpdate) ||
            hasPermission(UserRolePermission.InstitutionsAssignmentRetrieve)
    }

    fun canViewControllerInstitutionDetails(institutionId: Long): Boolean {
        return (hasPermission(UserRolePermission.InstitutionsRetrieve) && isAssignedToInstitution(institutionId)) ||
            hasPermission(UserRolePermission.InstitutionsUnlimited)
    }

    fun canUpdateControllerInstitution(institutionId: Long): Boolean {
        return (hasPermission(UserRolePermission.InstitutionsUpdate) && isAssignedToInstitution(institutionId)) ||
            hasPermission(UserRolePermission.InstitutionsUnlimited) && hasPermission(UserRolePermission.InstitutionsUpdate)
    }

    fun canCreateControllerInstitution(): Boolean {
        return hasPermission(UserRolePermission.InstitutionsUnlimited) && hasPermission(UserRolePermission.InstitutionsUpdate)
    }

    private fun isAssignedToInstitution(institutionId: Long): Boolean {
        val currentUserId = securityService.getUserIdOrThrow()
        return controllerInstitutionPersistence.getInstitutionUserByInstitutionIdAndUserId(
            institutionId = institutionId,
            userId = currentUserId
        ).isPresent
    }
}
