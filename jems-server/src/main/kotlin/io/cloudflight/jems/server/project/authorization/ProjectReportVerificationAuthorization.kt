package io.cloudflight.jems.server.project.authorization

import io.cloudflight.jems.server.authentication.authorization.Authorization
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.report.project.base.ProjectReportPersistence
import io.cloudflight.jems.server.user.service.model.UserRolePermission
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Component

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectReportVerificationAuthorization.canViewReportVerificationOverview(#projectId, #reportId)")
annotation class CanViewReportVerificationOverview

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectReportVerificationAuthorization.canViewReportVerificationCommunication(#projectId)")
annotation class CanViewReportVerificationCommunication

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectReportVerificationAuthorization.canEditReportVerificationCommunication(#projectId)")
annotation class CanEditReportVerificationCommunication

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectAuthorization.hasPermission('ProjectReportingVerificationProjectView', #projectId)")
annotation class CanViewReportVerificationPrivileged

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectAuthorization.hasPermission('ProjectReportingVerificationProjectEdit', #projectId)")
annotation class CanEditReportVerificationPrivileged

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectReportVerificationAuthorization.canViewReportVerificationPrivilegedByReportId(#projectReportId)")
annotation class CanViewReportVerificationPrivilegedByReportId

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectReportVerificationAuthorization.canEditReportVerificationPrivilegedByReportId(#projectReportId)")
annotation class CanEditReportVerificationPrivilegedByReportId


@Component
class ProjectReportVerificationAuthorization(
    override val securityService: SecurityService,
    private val projectPersistence: ProjectPersistence,
    private val projectReportPersistence: ProjectReportPersistence,
    private val authorizationUtilService: AuthorizationUtilService
) : Authorization(securityService) {


    fun canViewReportVerificationOverview(projectId:Long, reportId: Long): Boolean {
        val report = projectReportPersistence.getReportById(projectId, reportId)
        val project = projectPersistence.getApplicantAndStatusById(projectId)
        val currentUserId = securityService.getUserIdOrThrow()

        val canCreatorView = report.status.isFinalized() && isActiveUserIdEqualToOneOf(project.getUserIdsWithViewLevel()) &&
            !authorizationUtilService.userIsPartnerCollaboratorForProject(currentUserId, projectId)

        val canMonitorView = hasPermission(UserRolePermission.ProjectReportingVerificationProjectView, projectId)
        return canCreatorView || canMonitorView
    }

    fun canViewReportVerificationCommunication(projectId:Long): Boolean {
        val project = projectPersistence.getApplicantAndStatusById(projectId)
        val currentUserId = securityService.getUserIdOrThrow()

        val canMonitorView = hasPermission(UserRolePermission.ProjectReportingVerificationProjectView, projectId)
        val canManagerView = isActiveUserIdEqualToOneOf(project.getUserIdsWithViewLevel()) &&
            !authorizationUtilService.userIsPartnerCollaboratorForProject(currentUserId, projectId)

        return canMonitorView || canManagerView
    }

    fun canEditReportVerificationCommunication(projectId:Long): Boolean {
        val project = projectPersistence.getApplicantAndStatusById(projectId)
        val currentUserId = securityService.getUserIdOrThrow()

        val canMonitorEdit = hasPermission(UserRolePermission.ProjectReportingVerificationProjectEdit, projectId)
        val canManagerEdit = isActiveUserIdEqualToOneOf(project.getUserIdsWithEditLevel()) &&
            !authorizationUtilService.userIsPartnerCollaboratorForProject(currentUserId, projectId)

        return canMonitorEdit || canManagerEdit
    }

    fun canViewReportVerificationPrivilegedByReportId(projectReportId: Long): Boolean =
        hasPermission(UserRolePermission.ProjectReportingVerificationProjectView, projectReportId.toProjectId())

    fun canEditReportVerificationPrivilegedByReportId(projectReportId: Long): Boolean =
        hasPermission(UserRolePermission.ProjectReportingVerificationProjectEdit, projectReportId.toProjectId())

    private fun Long.toProjectId() = projectReportPersistence.getProjectIdForProjectReportId(this)


}
