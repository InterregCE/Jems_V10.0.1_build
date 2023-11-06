package io.cloudflight.jems.server.project.authorization

import io.cloudflight.jems.server.authentication.authorization.Authorization
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.controllerInstitution.service.ControllerInstitutionPersistence
import io.cloudflight.jems.server.project.repository.auditAndControl.correction.AuditControlCorrectionRepository
import io.cloudflight.jems.server.project.repository.partneruser.UserPartnerCollaboratorRepository
import io.cloudflight.jems.server.project.repository.projectuser.UserProjectCollaboratorRepository
import io.cloudflight.jems.server.user.service.model.UserRolePermission
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional


@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectAuditControlAuthorization.canViewAuditAndControl(#projectId)")
annotation class CanViewProjectAuditAndControl

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectAuditControlAuthorization.canEditAuditAndControl(#projectId)")
annotation class CanEditProjectAuditAndControl

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectAuditControlAuthorization.canViewProjectCorrection(#correctionId)")
annotation class CanViewProjectCorrection

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectAuditControlAuthorization.canEditProjectCorrection(#correctionId)")
annotation class CanEditProjectCorrection

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectAuditControlAuthorization.canCloseAuditAndControl(#projectId)")
annotation class CanCloseProjectAuditAndControl

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectAuditControlAuthorization.canCloseAuditAndControlCorrection(#projectId)")
annotation class CanCloseProjectAuditAndControlCorrection

@Component
class ProjectAuditControlAuthorization(
    override val securityService: SecurityService,
    private val partnerCollaboratorRepository: UserPartnerCollaboratorRepository,
    private val projectCollaboratorRepository: UserProjectCollaboratorRepository,
    private val controllerInstitutionPersistence: ControllerInstitutionPersistence,
    private val correctionRepository: AuditControlCorrectionRepository
) : Authorization(securityService) {

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

    fun canEditAuditAndControl(projectId: Long): Boolean {
        // monitor users
        if (hasPermission(UserRolePermission.ProjectMonitorAuditAndControlEdit, projectId)) {
            return true
        }

        // controllers
        val partnerControllers = controllerInstitutionPersistence.getRelatedUserIdsForProject(projectId)
        return isActiveUserIdEqualToOneOf(partnerControllers) && hasNonProjectAuthority(UserRolePermission.ProjectMonitorAuditAndControlEdit)
    }

    @Transactional(readOnly = true)
    fun canViewProjectCorrection(correctionId: Long): Boolean {
        val correctionEntity = correctionRepository.getById(correctionId)
        return canViewAuditAndControl(correctionEntity.auditControlEntity.projectId)
    }

    @Transactional(readOnly = true)
    fun canEditProjectCorrection(correctionId: Long): Boolean {
        val correctionEntity = correctionRepository.getById(correctionId)
        return canEditAuditAndControl(correctionEntity.auditControlEntity.projectId)
    }

    fun canCloseAuditAndControl(projectId: Long): Boolean {
        return canEditAuditAndControl(projectId) && hasNonProjectAuthority(UserRolePermission.ProjectMonitorCloseAuditControl)
    }

    fun canCloseAuditAndControlCorrection(projectId: Long): Boolean {
        return canEditAuditAndControl(projectId) && hasNonProjectAuthority(UserRolePermission.ProjectMonitorCloseAuditControlCorrection)
    }
}


