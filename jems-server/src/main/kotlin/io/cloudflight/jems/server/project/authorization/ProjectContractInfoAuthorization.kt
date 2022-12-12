package io.cloudflight.jems.server.project.authorization

import io.cloudflight.jems.server.authentication.authorization.Authorization
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.user.service.model.UserRolePermission
import org.springframework.stereotype.Component

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
            isActiveUserIdEqualToOneOf(applicantAndStatus.getUserIdsWithViewLevel()) ||
            authorizationUtilService.userIsPartnerCollaboratorForProject(userId = currentUserId, projectId = projectId)
    }

    fun canEditContractInfo(projectId: Long): Boolean {
        val applicantAndStatus = projectPersistence.getApplicantAndStatusById(projectId)
        return hasPermissionForProject(UserRolePermission.ProjectContractsEdit, projectId) ||
            isActiveUserIdEqualToOneOf(applicantAndStatus.getUserIdsWithEditLevel())
    }

}
