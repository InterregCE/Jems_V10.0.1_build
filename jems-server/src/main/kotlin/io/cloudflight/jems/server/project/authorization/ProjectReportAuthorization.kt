package io.cloudflight.jems.server.project.authorization

import io.cloudflight.jems.server.authentication.authorization.Authorization
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.controllerInstitution.ControllerInstitutionPersistence
import io.cloudflight.jems.server.controllerInstitution.service.model.UserInstitutionAccessLevel
import io.cloudflight.jems.server.project.entity.partneruser.PartnerCollaboratorLevel
import io.cloudflight.jems.server.project.entity.partneruser.PartnerCollaboratorLevel.EDIT
import io.cloudflight.jems.server.project.entity.partneruser.PartnerCollaboratorLevel.VIEW
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.partner.UserPartnerCollaboratorPersistence
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Component

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectReportAuthorization.canEditPartnerReport(#partnerId)")
annotation class CanEditPartnerReport

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectReportAuthorization.canViewPartnerReport(#partnerId)")
annotation class CanViewPartnerReport

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectReportAuthorization.canEditPartnerControlReport(#partnerId)")
annotation class CanEditPartnerControlReport

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectReportAuthorization.canViewPartnerControlReport(#partnerId)")
annotation class CanViewPartnerControlReport

@Component
class ProjectReportAuthorization(
    override val securityService: SecurityService,
    private val partnerPersistence: PartnerPersistence,
    private val partnerCollaboratorPersistence: UserPartnerCollaboratorPersistence,
    private val controllerInstitutionPersistence: ControllerInstitutionPersistence,
) : Authorization(securityService) {

    fun canEditPartnerReport(partnerId: Long): Boolean =
        hasPermissionForPartner(partnerId = partnerId, EDIT)

    fun canViewPartnerReport(partnerId: Long): Boolean =
        hasPermissionForPartner(partnerId = partnerId, VIEW)

    private fun hasPermissionForPartner(partnerId: Long, levelNeeded: PartnerCollaboratorLevel): Boolean {
        val projectId = partnerPersistence.getProjectIdForPartnerId(partnerId)

        // monitor users
        if (hasPermissionForProject(levelNeeded.correspondingRolePermission, projectId))
            return true

        // creators
        val level = getLevelForUserCreatorAndPartner(userId = securityService.getUserIdOrThrow(), partnerId = partnerId)

        return if (levelNeeded == VIEW)
            level.isPresent
        else
            level.isPresent && level.get() == EDIT
    }

    private fun getLevelForUserCreatorAndPartner(userId: Long, partnerId: Long) =
        partnerCollaboratorPersistence.findByUserIdAndPartnerId(
            userId = userId,
            partnerId = partnerId,
        )

    fun canEditPartnerControlReport(partnerId: Long): Boolean =
        controllerInstitutionPersistence.getControllerUserAccessLevelForPartner(
            userId = securityService.getUserIdOrThrow(),
            partnerId = partnerId,
        ) == UserInstitutionAccessLevel.Edit

    fun canViewPartnerControlReport(partnerId: Long): Boolean =
        controllerInstitutionPersistence.getControllerUserAccessLevelForPartner(
            userId = securityService.getUserIdOrThrow(),
            partnerId = partnerId,
        ) != null

}
