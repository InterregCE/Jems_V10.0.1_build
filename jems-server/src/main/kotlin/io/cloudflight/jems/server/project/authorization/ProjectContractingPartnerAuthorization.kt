package io.cloudflight.jems.server.project.authorization

import io.cloudflight.jems.server.authentication.authorization.Authorization
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.project.entity.partneruser.PartnerCollaboratorLevel
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.partner.UserPartnerCollaboratorPersistence
import io.cloudflight.jems.server.user.service.model.UserRolePermission
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Component

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectContractingPartnerAuthorization.hasViewPermission(#partnerId)")
annotation class CanRetrieveProjectContractingPartner

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectContractingPartnerAuthorization.hasEditPermission(#partnerId)")
annotation class CanUpdateProjectContractingPartner

@Component
class ProjectContractingPartnerAuthorization(
    override val securityService: SecurityService,
    val partnerPersistence: PartnerPersistence,
    val projectPersistence: ProjectPersistence,
    val partnerCollaboratorPersistence: UserPartnerCollaboratorPersistence,
    val projectAuthorization: ProjectAuthorization
): Authorization(securityService) {

    fun hasViewPermission(partnerId: Long): Boolean {
        val projectId = partnerPersistence.getProjectIdForPartnerId(partnerId)
        val hasProjectPermission = projectAuthorization.hasPermission(UserRolePermission.ProjectContractingPartnerView, projectId)

        val level = partnerCollaboratorPersistence.findByUserIdAndPartnerId(
            userId = securityService.getUserIdOrThrow(),
            partnerId = partnerId,
        )
        return level.isPresent || hasProjectPermission
    }

    fun hasEditPermission(partnerId: Long): Boolean {
        val projectId = partnerPersistence.getProjectIdForPartnerId(partnerId)
        val hasProjectPermission = projectAuthorization.hasPermission(UserRolePermission.ProjectContractingPartnerEdit, projectId)

        val level = partnerCollaboratorPersistence.findByUserIdAndPartnerId(
            userId = securityService.getUserIdOrThrow(),
            partnerId = partnerId,
        )
        return (level.isPresent && level.get() == PartnerCollaboratorLevel.EDIT) || hasProjectPermission
    }
}
