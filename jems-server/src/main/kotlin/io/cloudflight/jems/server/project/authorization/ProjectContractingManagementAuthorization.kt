package io.cloudflight.jems.server.project.authorization

import io.cloudflight.jems.server.authentication.authorization.Authorization
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.user.service.model.UserRolePermission
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Component


@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectContractingManagementAuthorization.canViewProjectManagement(#projectId)")
annotation class CanViewProjectManagement

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectContractingManagementAuthorization.canEditProjectManagement(#projectId)")
annotation class CanEditProjectManagement

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectAuthorization.hasPermission('ProjectContractingReportingView', #projectId) " +
    "|| @projectContractingManagementAuthorization.canViewReportingAndIsCollaborator(#projectId)")
annotation class CanRetrieveProjectContractingReporting

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectAuthorization.hasPermission('ProjectContractingReportingEdit', #projectId) " +
    "|| @projectContractingManagementAuthorization.canEditReportingAndIsCollaborator(#projectId)")
annotation class CanUpdateProjectContractingReporting

@Component
class ProjectContractingManagementAuthorization(
    override val securityService: SecurityService,
    val partnerPersistence: PartnerPersistence,
    val projectPersistence: ProjectPersistence,
    val authorizationUtilService: AuthorizationUtilService
): Authorization(securityService) {

    fun canViewProjectManagement(projectId: Long): Boolean {
        val applicantAndStatus = projectPersistence.getApplicantAndStatusById(projectId)

        return hasPermissionForProject(UserRolePermission.ProjectContractingManagementView, projectId) ||
            hasPermissionForProject(UserRolePermission.ProjectContractingManagementEdit, projectId) ||
            isActiveUserIdEqualToOneOf(applicantAndStatus.getUserIdsWithViewLevel()) ||
            authorizationUtilService.userIsPartnerCollaboratorForProject(
                userId = securityService.getUserIdOrThrow(),
                projectId = projectId)
    }

    fun canEditProjectManagement(projectId: Long): Boolean {
        val project = projectPersistence.getApplicantAndStatusById(projectId)
        return hasPermissionForProject(UserRolePermission.ProjectContractingManagementEdit, projectId) ||
            isActiveUserIdEqualToOneOf(project.getUserIdsWithEditLevel())
    }

    fun canEditReportingAndIsCollaborator(projectId: Long): Boolean {
        val applicantAndStatus = projectPersistence.getApplicantAndStatusById(projectId)
        return hasPermission(UserRolePermission.ProjectCreatorContractingReportingEdit) &&
            isActiveUserIdEqualToOneOf(applicantAndStatus.getUserIdsWithViewLevel())
    }


    fun canViewReportingAndIsCollaborator(projectId: Long): Boolean {
        val applicantAndStatus = projectPersistence.getApplicantAndStatusById(projectId)
        return hasPermission(UserRolePermission.ProjectCreatorContractingReportingView) &&
            isActiveUserIdEqualToOneOf(applicantAndStatus.getUserIdsWithViewLevel())
    }

}
