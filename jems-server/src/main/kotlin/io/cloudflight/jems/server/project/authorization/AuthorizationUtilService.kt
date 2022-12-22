package io.cloudflight.jems.server.project.authorization

import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.controllerInstitution.service.ControllerInstitutionPersistence
import io.cloudflight.jems.server.project.service.partner.UserPartnerCollaboratorPersistence
import io.cloudflight.jems.server.user.service.model.UserRolePermission
import io.cloudflight.jems.server.user.service.model.assignment.PartnerCollaborator
import org.springframework.stereotype.Component

@Component
class AuthorizationUtilService(
    val partnerCollaboratorPersistence: UserPartnerCollaboratorPersistence,
    val controllerInstitutionPersistence: ControllerInstitutionPersistence,
    val securityService: SecurityService
) {

    fun userIsPartnerCollaboratorForProject(userId: Long, projectId: Long): Boolean =
        getUserPartnerCollaborations(userId = userId, projectId = projectId).isNotEmpty()

    private fun getUserPartnerCollaborations(userId: Long, projectId: Long): Set<PartnerCollaborator> =
        partnerCollaboratorPersistence.findPartnersByUserAndProject(
            userId = userId,
            projectId = projectId,
        )

    fun hasPermissionAsController(permission: UserRolePermission, projectId: Long): Boolean {
        val partnerControllers = controllerInstitutionPersistence.getRelatedUserIdsForProject(projectId)

        return securityService.currentUser?.hasPermission(permission)!!
            && partnerControllers.contains(securityService.getUserIdOrThrow())
    }

}
