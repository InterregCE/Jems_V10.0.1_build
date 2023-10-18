package io.cloudflight.jems.server.project.authorization

import io.cloudflight.jems.server.authentication.authorization.Authorization
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.controllerInstitution.service.ControllerInstitutionPersistence
import io.cloudflight.jems.server.project.repository.partneruser.UserPartnerCollaboratorRepository
import io.cloudflight.jems.server.project.repository.projectuser.UserProjectCollaboratorRepository
import io.cloudflight.jems.server.user.service.model.UserRolePermission
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Component


@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectAuditControlAuthorization.canViewAuditAndControl(#projectId)")
annotation class CanViewProjectAuditAndControl


@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectAuditControlAuthorization.canEditAuditAndControl(#projectId)")
annotation class CanEditProjectAuditAndControl

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectAuditControlAuthorization.canCloseAuditAndControl(#projectId)")
annotation class CanCloseProjectAuditAndControl

@Component
class ProjectAuditControlAuthorization(
    override val securityService: SecurityService,
    private val partnerCollaboratorRepository: UserPartnerCollaboratorRepository,
    private val projectCollaboratorRepository: UserProjectCollaboratorRepository,
    private val controllerInstitutionPersistence: ControllerInstitutionPersistence,
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

    fun canCloseAuditAndControl(projectId: Long): Boolean {
        return canEditAuditAndControl(projectId) && hasNonProjectAuthority(UserRolePermission.ProjectMonitorCloseAuditControl)
    }
}


