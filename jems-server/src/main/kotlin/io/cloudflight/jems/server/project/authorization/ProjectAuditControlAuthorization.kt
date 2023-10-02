package io.cloudflight.jems.server.project.authorization

import io.cloudflight.jems.server.authentication.authorization.Authorization
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.project.repository.partneruser.UserPartnerCollaboratorRepository
import io.cloudflight.jems.server.project.repository.projectuser.UserProjectCollaboratorRepository
import io.cloudflight.jems.server.user.service.model.UserRolePermission
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Component


@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectAuditControlAuthorization.canViewAuditAndControl(#projectId)")
annotation class CanViewProjectAuditAndControl


@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectAuditControlAuthorization.hasPermission('ProjectMonitorAuditAndControlEdit', #projectId)")
annotation class CanEditProjectAuditAndControl


@Component
class ProjectAuditControlAuthorization(
    override val securityService: SecurityService,
    private val partnerCollaboratorRepository: UserPartnerCollaboratorRepository,
    private val projectCollaboratorRepository: UserProjectCollaboratorRepository,
) : Authorization(securityService) {



    fun canViewAuditAndControl(projectId: Long): Boolean {

        if (hasPermission(UserRolePermission.ProjectMonitorAuditAndControlView, projectId)) {
            return true
        }
        val partnerCollaborators = partnerCollaboratorRepository.findAllByProjectId(projectId).mapTo(HashSet()) { it.id.userId }
        val projectCollaborators = projectCollaboratorRepository.findAllByIdProjectId(projectId).mapTo(HashSet()) { it.id.userId }
        val collaborators = partnerCollaborators union projectCollaborators

        return isActiveUserIdEqualToOneOf(collaborators)
    }
}


