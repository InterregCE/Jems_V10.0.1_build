package io.cloudflight.jems.server.project.service.report.partner

import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.project.service.partner.UserPartnerCollaboratorPersistence
import io.cloudflight.jems.server.user.service.model.UserRolePermission
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SensitiveDataAuthorizationService(
    private val securityService: SecurityService,
    private val partnerCollaboratorPersistence: UserPartnerCollaboratorPersistence,
) {

    @Transactional(readOnly = true)
    fun canViewPartnerSensitiveData(partnerId: Long) =
        isCurrentUserCollaboratorWithSensitiveFor(partnerId) || hasGlobalMonitorView()

    @Transactional(readOnly = true)
    fun canEditPartnerSensitiveData(partnerId: Long) =
        isCurrentUserCollaboratorWithSensitiveFor(partnerId) || hasGlobalMonitorEdit()

    @Transactional(readOnly = true)
    fun isCurrentUserCollaboratorWithSensitiveFor(partnerId: Long) =
        partnerCollaboratorPersistence.canUserSeePartnerSensitiveData(securityService.getUserIdOrThrow(), partnerId)

    private fun hasGlobalMonitorView(): Boolean {
        return securityService.currentUser?.hasPermission(UserRolePermission.ProjectReportingView) ?: false
    }
    private fun hasGlobalMonitorEdit(): Boolean {
        return securityService.currentUser?.hasPermission(UserRolePermission.ProjectReportingEdit) ?: false
    }
}