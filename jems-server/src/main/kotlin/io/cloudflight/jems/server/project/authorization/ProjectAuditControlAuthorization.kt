package io.cloudflight.jems.server.project.authorization

import io.cloudflight.jems.server.authentication.authorization.Authorization
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.controllerInstitution.service.ControllerInstitutionPersistence
import io.cloudflight.jems.server.project.repository.auditAndControl.correction.AuditControlCorrectionRepository
import io.cloudflight.jems.server.project.repository.partneruser.UserPartnerCollaboratorRepository
import io.cloudflight.jems.server.project.repository.projectuser.UserProjectCollaboratorRepository
import io.cloudflight.jems.server.project.service.auditAndControl.AuditControlPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.correction.AuditControlCorrectionPersistence
import io.cloudflight.jems.server.user.service.model.UserRolePermission
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional


@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectAuditControlAuthorization.canViewAuditControlViaAuditControlId(#auditControlId)")
annotation class CanViewAuditControl

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectAuditControlAuthorization.canViewAuditAndControl(#projectId)")
annotation class CanViewAuditControlForProject

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectAuditControlAuthorization.canEditAuditControlViaAuditControlId(#auditControlId)")
annotation class CanEditAuditControl

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectAuditControlAuthorization.canEditAuditAndControl(#projectId)")
annotation class CanEditAuditControlForProject

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectAuditControlAuthorization.canCloseAuditAndControl(#auditControlId)")
annotation class CanCloseAuditControl


@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectAuditControlAuthorization.canViewProjectCorrection(#correctionId)")
annotation class CanViewAuditControlCorrection

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectAuditControlAuthorization.canEditProjectCorrection(#correctionId)")
annotation class CanEditAuditControlCorrection

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectAuditControlAuthorization.canCloseAuditAndControlCorrection(#correctionId)")
annotation class CanCloseAuditControlCorrection

@Component
class ProjectAuditControlAuthorization(
    override val securityService: SecurityService,
    private val partnerCollaboratorRepository: UserPartnerCollaboratorRepository,
    private val projectCollaboratorRepository: UserProjectCollaboratorRepository,
    private val controllerInstitutionPersistence: ControllerInstitutionPersistence,
    private val auditControlPersistence: AuditControlPersistence,
    private val auditControlCorrectionPersistence: AuditControlCorrectionPersistence,
) : Authorization(securityService) {

    fun canViewAuditControlViaAuditControlId(auditControlId: Long): Boolean {
        val projectId = getProjectIdFromAuditControlId(auditControlId)
        return canViewAuditAndControl(projectId = projectId)
    }

    fun canViewAuditAndControl(projectId: Long): Boolean {
        // monitor users
        if (hasPermission(UserRolePermission.ProjectMonitorAuditAndControlView, projectId)) {
            return true
        }

        // partner collaborators and project collaborators
        val partnerCollaborators = partnerCollaboratorRepository.findAllByProjectId(projectId).mapTo(HashSet()) { it.id.userId }
        val projectCollaborators = projectCollaboratorRepository.findAllByIdProjectId(projectId).mapTo(HashSet()) { it.id.userId }
        val collaborators = partnerCollaborators union projectCollaborators
        if (isActiveUserIdEqualToOneOf(collaborators))
            return true

        // controllers
        val partnerControllers = controllerInstitutionPersistence.getRelatedUserIdsForProject(projectId)
        return isActiveUserIdEqualToOneOf(partnerControllers) && hasNonProjectAuthority(UserRolePermission.ProjectMonitorAuditAndControlView)
    }

    fun canEditAuditControlViaAuditControlId(auditControlId: Long): Boolean {
        val projectId = getProjectIdFromAuditControlId(auditControlId)
        return canEditAuditAndControl(projectId = projectId)
    }

    fun canEditAuditAndControl(projectId: Long): Boolean {
        // monitor users
        if (hasPermission(UserRolePermission.ProjectMonitorAuditAndControlEdit, projectId)) {
            return true
        }

        // controllers
        val partnerControllers = controllerInstitutionPersistence.getRelatedUserIdsForProject(projectId)
        return isActiveUserIdEqualToOneOf(partnerControllers) && hasNonProjectAuthority(UserRolePermission.ProjectMonitorAuditAndControlEdit)
    }

    fun canViewProjectCorrection(correctionId: Long): Boolean =
        canViewAuditAndControl(projectId = getProjectIdFromCorrectionId(correctionId = correctionId))

    fun canEditProjectCorrection(correctionId: Long): Boolean =
        canEditAuditAndControl(projectId = getProjectIdFromCorrectionId(correctionId = correctionId))

    fun canCloseAuditAndControl(auditControlId: Long): Boolean {
        return canEditAuditControlViaAuditControlId(auditControlId = auditControlId)
                && hasNonProjectAuthority(UserRolePermission.ProjectMonitorCloseAuditControl)
    }

    fun canCloseAuditAndControlCorrection(correctionId: Long): Boolean =
        canEditProjectCorrection(correctionId)
                && hasNonProjectAuthority(UserRolePermission.ProjectMonitorCloseAuditControlCorrection)

    private fun getProjectIdFromAuditControlId(auditControlId: Long) =
        auditControlPersistence.getProjectIdForAuditControl(auditControlId = auditControlId)

    private fun getProjectIdFromCorrectionId(correctionId: Long) =
        auditControlCorrectionPersistence.getProjectIdForCorrection(correctionId = correctionId)

}


