package io.cloudflight.jems.server.project.authorization

import io.cloudflight.jems.server.authentication.authorization.Authorization
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.user.service.model.UserRolePermission
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Component


@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectContractInfoAuthorization.canViewContractInfo(#projectId)")
annotation class CanViewContractInfo

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectContractInfoAuthorization.canEditContractInfo(#projectId)")
annotation class CanEditContractInfo

@Component
class ProjectContractInfoAuthorization(
    override val securityService: SecurityService,
    val projectPersistence: ProjectPersistence,
    val authorizationUtilService: AuthorizationUtilService
): Authorization(securityService) {

    fun canViewContractInfo(projectId: Long): Boolean{
        val currentUserId = securityService.getUserIdOrThrow()
        val applicantAndStatus = projectPersistence.getApplicantAndStatusById(projectId)
        return hasPermissionForProject(UserRolePermission.ProjectContractsView, projectId) ||
            hasPermissionForProject(UserRolePermission.ProjectContractsEdit, projectId) ||
            authorizationUtilService.userIsProjectOwnerOrProjectCollaborator(userId = currentUserId, applicantAndStatus) ||
            authorizationUtilService.userIsPartnerCollaboratorForProject(userId = currentUserId, projectId = projectId)
    }

    fun canEditContractInfo(projectId: Long): Boolean {
        val currentUserId = securityService.getUserIdOrThrow()
        val applicantAndStatus = projectPersistence.getApplicantAndStatusById(projectId)
        return hasPermissionForProject(UserRolePermission.ProjectContractsEdit, projectId) ||
            authorizationUtilService.userIsProjectCollaboratorWithEditPrivilege(userId = currentUserId, applicantAndStatus)
    }


}
